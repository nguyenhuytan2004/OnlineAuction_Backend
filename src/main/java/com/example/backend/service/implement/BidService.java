package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ConcurrentBidException;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.model.Email.EmailNotificationRequest.EmailType;
import com.example.backend.model.WebSocket.BidUpdateMessage.MessageType;
import com.example.backend.producer.EmailProducer;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IAuctionService;
import com.example.backend.service.IBidService;
import com.example.backend.service.IProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BidService implements IBidService {

  private static final int MAX_OPTIMISTIC_RETRY_ATTEMPTS = 3;
  private static final long BASE_RETRY_BACKOFF_MS = 50L;

  @Autowired
  private IBidRepository _bidRepository;
  @Autowired
  private IProductRepository _productRepository;
  @Autowired
  private IUserRepository _userRepository;
  @Autowired
  @Lazy
  private IProductService _productService;
  @Autowired
  private IAuctionService _auctionService;
  @Autowired
  private EmailProducer emailProducer;
  @Autowired
  private PlatformTransactionManager transactionManager;

  @Override
  public User getHighestBidderByProductId(Integer productId) {
    log.info(
        "[SERVICE][GET][HIGHEST_BIDDER] Input productId={}",
        productId);

    try {
      Bid highestBid = _bidRepository.findFirstByProductProductIdOrderByBidPriceDescBidAtAscBidIdAsc(productId);

      User bidder = highestBid != null ? highestBid.getBidder() : null;

      log.info(
          "[SERVICE][GET][HIGHEST_BIDDER] Output bidder={}",
          bidder);
      return bidder;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][HIGHEST_BIDDER] Error occurred (productId={}): {}",
          productId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public Bid placeBid(CreateBidRequest createBidRequest) throws Exception {
    log.info(
        "[SERVICE][POST][PLACE_BID] Input request={}",
        createBidRequest);

    try {
      for (int attempt = 1; attempt <= MAX_OPTIMISTIC_RETRY_ATTEMPTS; attempt++) {
        try {
          Bid savedBid = executePlaceBidAttempt(createBidRequest);

          if (savedBid == null) {
            throw new IllegalStateException("Bid transaction returned empty result");
          }

          log.info(
              "[SERVICE][POST][PLACE_BID] Success bid={}, attempt={}",
              savedBid,
              attempt);
          return savedBid;

        } catch (OptimisticLockingFailureException e) {
          log.warn(
              "[SERVICE][POST][PLACE_BID] Optimistic lock conflict, attempt={}, productId={}, bidderId={}",
              attempt,
              createBidRequest.getProductId(),
              createBidRequest.getBidderId());

          if (attempt >= MAX_OPTIMISTIC_RETRY_ATTEMPTS) {
            throw buildConcurrentBidException(createBidRequest.getProductId(), e);
          }

          sleepBeforeRetry(attempt);
        }
      }

      throw new IllegalStateException("Unable to place bid after retries");

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][PLACE_BID] Error occurred (request={}): {}",
          createBidRequest,
          e.getMessage(),
          e);
      throw e;
    }
  }

  private Bid executePlaceBidAttempt(CreateBidRequest createBidRequest) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    return transactionTemplate.execute(status -> {
      Product product = _productRepository.findById(createBidRequest.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      User bidder = _userRepository.findById(createBidRequest.getBidderId())
          .orElseThrow(() -> new IllegalArgumentException("Bidder not found"));

      if (LocalDateTime.now().isAfter(product.getEndTime())) {
        throw new IllegalArgumentException("Auction has already ended");
      }

      if (!product.getIsActive()) {
        throw new IllegalArgumentException("Product is not active");
      }

      Boolean isEligible = _productService.checkBiddingEligibility(
          product.getProductId(),
          bidder.getUserId());
      if (!isEligible) {
        throw new IllegalArgumentException(
            "Bidder is not eligible to place a bid on this product");
      }

      if (bidder.getUserId().equals(product.getSeller().getUserId())) {
        throw new IllegalArgumentException("Seller cannot bid on their own product");
      }

      BigDecimal minRequiredMaxBid = product.getCurrentPrice().add(product.getPriceStep());

      if (createBidRequest.getMaxAutoPrice().compareTo(minRequiredMaxBid) < 0) {
        throw new IllegalArgumentException(
            "Max bid must be at least " + minRequiredMaxBid);
      }

      Bid savedBid = processAutoBid(product, bidder, createBidRequest);
      _auctionService.checkAndRenewAuction(product);
      return savedBid;
    });
  }

  private void sleepBeforeRetry(int attempt) {
    try {
      Thread.sleep(BASE_RETRY_BACKOFF_MS * attempt);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Thread interrupted while retrying bid", ie);
    }
  }

  private ConcurrentBidException buildConcurrentBidException(Integer productId, Throwable cause) {
    Product latestProduct = _productRepository.findById(productId).orElse(null);

    if (latestProduct == null) {
      return new ConcurrentBidException(
          "Concurrent bid detected. Please retry.",
          productId,
          null,
          null,
          null,
          cause);
    }

    Integer highestBidderId = latestProduct.getHighestBidder() != null
        ? latestProduct.getHighestBidder().getUserId()
        : null;

    return new ConcurrentBidException(
        "Concurrent bid detected. Please refresh price and retry.",
        productId,
        latestProduct.getCurrentPrice(),
        latestProduct.getPriceStep(),
        highestBidderId,
        cause);
  }

  // Extra method to process auto-bid logic
  private Bid processAutoBid(Product product, User bidder, CreateBidRequest request) {

    log.info(
        "[SERVICE][AUTO_BID][PROCESS] Input productId={}, bidderId={}, maxAutoPrice={}",
        product.getProductId(),
        bidder.getUserId(),
        request.getMaxAutoPrice());

    try {
      BigDecimal previousPrice = product.getCurrentPrice();
      User previousHighestBidder;

      Bid currentHighestBid = _bidRepository
          .findFirstByProductProductIdOrderByBidPriceDescBidAtAscBidIdAsc(product.getProductId());

      MessageType messageType;
      String message;
      Bid newBid = new Bid();

      if (currentHighestBid == null) {
        newBid.setBidder(bidder);
        newBid.setProduct(product);
        newBid.setBidPrice(product.getCurrentPrice());
        newBid.setMaxAutoPrice(request.getMaxAutoPrice());

        messageType = MessageType.NEWBID;
        message = bidder.getFullName()
            + " placed the first bid of "
            + newBid.getBidPrice();

        emailProducer.sendProductEmail(
            EmailType.BID_SUCCESS_WINNER,
            bidder.getUserId(),
            product.getProductId());
        emailProducer.sendProductEmail(
            EmailType.BID_SUCCESS_SELLER,
            product.getSeller().getUserId(),
            product.getProductId());

      } else if (currentHighestBid.getBidder().getUserId().equals(bidder.getUserId())) {

        currentHighestBid.setMaxAutoPrice(request.getMaxAutoPrice());
        Bid updatedBid = _bidRepository.save(currentHighestBid);

        log.info(
            "[SERVICE][AUTO_BID][UPDATE_MAX] productId={}, bidderId={}, newMaxAutoPrice={}",
            product.getProductId(),
            bidder.getUserId(),
            request.getMaxAutoPrice());

        emailProducer.sendProductEmail(
            EmailType.BID_SUCCESS_WINNER,
            currentHighestBid.getBidder().getUserId(),
            product.getProductId());
        emailProducer.sendProductEmail(
            EmailType.BID_SUCCESS_SELLER,
            product.getSeller().getUserId(),
            product.getProductId());

        log.info(
            "[SERVICE][AUTO_BID][PROCESS] Output bid={}",
            updatedBid);
        return updatedBid;

      } else {

        BigDecimal competitorMaxPrice = currentHighestBid.getMaxAutoPrice();
        BigDecimal bidderMaxPrice = request.getMaxAutoPrice();

        if (bidderMaxPrice.compareTo(competitorMaxPrice) <= 0) {

          newBid.setBidder(currentHighestBid.getBidder());
          newBid.setProduct(product);
          newBid.setBidPrice(bidderMaxPrice);
          newBid.setMaxAutoPrice(competitorMaxPrice);

          messageType = MessageType.OUTBID;
          message = bidder.getFullName()
              + " was outbid by "
              + currentHighestBid.getBidder().getFullName()
              + " with bid of "
              + newBid.getBidPrice();

          emailProducer.sendProductEmail(
              EmailType.BID_SUCCESS_WINNER,
              currentHighestBid.getBidder().getUserId(),
              product.getProductId());
          emailProducer.sendProductEmail(
              EmailType.BID_SUCCESS_SELLER,
              product.getSeller().getUserId(),
              product.getProductId());

        } else {

          previousHighestBidder = currentHighestBid.getBidder();

          newBid.setBidder(bidder);
          newBid.setProduct(product);
          newBid.setBidPrice(
              competitorMaxPrice.add(product.getPriceStep()));
          newBid.setMaxAutoPrice(bidderMaxPrice);

          messageType = MessageType.LEADING;
          message = bidder.getFullName()
              + " is now the highest bidder with bid of "
              + newBid.getBidPrice();

          emailProducer.sendProductEmail(
              EmailType.BID_SUCCESS_PREVIOUS_BIDDER,
              previousHighestBidder.getUserId(),
              product.getProductId());
          emailProducer.sendProductEmail(
              EmailType.BID_SUCCESS_WINNER,
              bidder.getUserId(),
              product.getProductId());
          emailProducer.sendProductEmail(
              EmailType.BID_SUCCESS_SELLER,
              product.getSeller().getUserId(),
              product.getProductId());
        }
      }

      Bid savedBid = _bidRepository.save(newBid);

      product.setCurrentPrice(savedBid.getBidPrice());
      product.setHighestBidder(savedBid.getBidder());
      product.setBidCount(product.getBidCount() + 1);
      _productRepository.save(product);

      _auctionService.broadcastAuctionUpdate(
          product,
          savedBid,
          previousPrice,
          messageType,
          message);

      log.info(
          "[SERVICE][AUTO_BID][PROCESS] Success productId={}, bidId={}, currentPrice={}",
          product.getProductId(),
          savedBid.getBidId(),
          savedBid.getBidPrice());

      return savedBid;

    } catch (Exception e) {
      log.error(
          "[SERVICE][AUTO_BID][PROCESS] Error occurred (productId={}, bidderId={}): {}",
          product.getProductId(),
          bidder.getUserId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public List<Bid> getTop5BidsByProductId(Integer productId) {
    log.info(
        "[SERVICE][GET][TOP5_BIDS] Input productId={}",
        productId);

    try {
      List<Bid> bids = _bidRepository.findTop5ByProductProductIdOrderByBidPriceDescBidAtAsc(productId);

      log.info(
          "[SERVICE][GET][TOP5_BIDS] Output bids={}",
          bids);
      return bids;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][TOP5_BIDS] Error occurred (productId={}): {}",
          productId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public Bid getBid(Integer bidId) {
    log.info(
        "[SERVICE][GET][BID] Input bidId={}",
        bidId);

    try {
      Bid bid = _bidRepository.findById(bidId)
          .orElseThrow(() -> new IllegalArgumentException("Bid not found"));

      log.info(
          "[SERVICE][GET][BID] Output bid={}",
          bid);
      return bid;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][BID] Error occurred (bidId={}): {}",
          bidId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public void removeBidsByProductIdAndBidderId(Integer productId, Integer bidderId) {
    log.info(
        "[SERVICE][DELETE][BIDS_BY_BIDDER] Input productId={}, bidderId={}",
        productId,
        bidderId);

    try {
      _bidRepository.deleteByProductProductIdAndBidderUserId(productId, bidderId);

      Bid highestBid = _bidRepository.findFirstByProductProductIdOrderByBidPriceDescBidAtAscBidIdAsc(productId);

      if (highestBid != null) {
        Product product = highestBid.getProduct();
        product.setHighestBidder(highestBid.getBidder());
        product.setCurrentPrice(highestBid.getBidPrice());
        _productRepository.save(product);

        log.info(
            "[SERVICE][DELETE][BIDS_BY_BIDDER] Updated productId={}, highestBidderId={}, currentPrice={}",
            productId,
            highestBid.getBidder().getUserId(),
            highestBid.getBidPrice());
      }

      log.info(
          "[SERVICE][DELETE][BIDS_BY_BIDDER] Success productId={}, bidderId={}",
          productId,
          bidderId);

    } catch (Exception e) {
      log.error(
          "[SERVICE][DELETE][BIDS_BY_BIDDER] Error occurred (productId={}, bidderId={}): {}",
          productId,
          bidderId,
          e.getMessage(),
          e);
      throw e;
    }
  }
}
