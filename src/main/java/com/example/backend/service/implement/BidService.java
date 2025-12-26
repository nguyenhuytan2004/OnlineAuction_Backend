package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.producer.EmailProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.model.WebSocket.BidUpdateMessage.MessageType;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IAuctionService;
import com.example.backend.service.IBidService;
import com.example.backend.service.IProductService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BidService implements IBidService {

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

  @Override
  public User getHighestBidderByProductId(Integer productId) {
    Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(productId);

    if (highestBid == null) {
      return null;
    }

    Bid bid = highestBid;
    User bidder = bid.getBidder();

    return bidder;
  }

  @Override
  public Bid placeBid(CreateBidRequest createBidRequest) throws Exception {
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

    Boolean isEligible = _productService.checkBiddingEligibility(product.getProductId(), bidder.getUserId());
    if (!isEligible) {
      throw new IllegalArgumentException("Bidder is not eligible to place a bid on this product");
    }

    if (bidder.getUserId().equals(product.getSeller().getUserId())) {
      throw new IllegalArgumentException("Seller cannot bid on their own product");
    }

    BigDecimal minRequiredMaxBid = product.getCurrentPrice().add(product.getPriceStep());
    if (createBidRequest.getMaxAutoPrice().compareTo(minRequiredMaxBid) < 0) {
      throw new IllegalArgumentException("Max bid must be at least " + minRequiredMaxBid);
    }

    Bid savedBid = processAutoBid(product, bidder, createBidRequest);
    _auctionService.checkAndRenewAuction(product);

    return savedBid;
  }

  // Extra method to process auto-bid logic
  @Transactional
  private Bid processAutoBid(Product product, User bidder, CreateBidRequest request) {
    log.info("[AUTO-BID] Processing bid for product {} by user {} with maxPrice {}",
        product.getProductId(), bidder.getUserId(), request.getMaxAutoPrice());

    BigDecimal previousPrice = product.getCurrentPrice();
    User previousHighestBidder = null;

    // Lấy bid có bidPrice cao nhất
    Bid currentHighestBid = _bidRepository
        .findTopByProductProductIdOrderByBidPriceDesc(product.getProductId());

    MessageType messageType;
    String message;
    Bid newBid = new Bid();

    // Chưa có ai đặt giá
    if (currentHighestBid == null) {
      newBid.setBidder(bidder);
      newBid.setProduct(product);
      newBid.setBidPrice(product.getCurrentPrice());
      newBid.setMaxAutoPrice(request.getMaxAutoPrice());

      messageType = MessageType.NEWBID;
      message = bidder.getFullName() + " placed the first bid of "
          + newBid.getBidPrice();

      emailProducer.sendBidSuccess(bidder.getUserId(), product.getProductId());
      emailProducer.sendBidSuccess(product.getSeller().getUserId(), product.getProductId());
    } else {
      // Người đặt giá cao nhất là người hiện tại, cập nhật lại maxAutoPrice
      if (currentHighestBid.getBidder().getUserId().equals(bidder.getUserId())) {
        currentHighestBid.setMaxAutoPrice(request.getMaxAutoPrice());
        Bid updatedBid = _bidRepository.save(currentHighestBid);

        log.info("[AUTO-BID] Updated existing highest bid for same bidder: new maxAutoPrice={}",
            request.getMaxAutoPrice());

        emailProducer.sendBidSuccess(currentHighestBid.getBidder().getUserId(), product.getProductId());
        emailProducer.sendBidSuccess(product.getSeller().getUserId(), product.getProductId());

        return updatedBid;
      }

      BigDecimal competitorMaxPrice = currentHighestBid.getMaxAutoPrice();
      BigDecimal bidderMaxPrice = request.getMaxAutoPrice();
      // Người đặt giá hiện tại không vuot qua được người giữ giá cao nhất
      if (bidderMaxPrice.compareTo(competitorMaxPrice) <= 0) {
        newBid.setBidder(currentHighestBid.getBidder());
        newBid.setProduct(product);
        newBid.setBidPrice(bidderMaxPrice);
        newBid.setMaxAutoPrice(competitorMaxPrice);

        messageType = MessageType.OUTBID;
        message = bidder.getFullName() + "was outbid by "
            + currentHighestBid.getBidder().getFullName() + " with bid of "
            + newBid.getBidPrice();

        emailProducer.sendBidSuccess(currentHighestBid.getBidder().getUserId(), product.getProductId());
        emailProducer.sendBidSuccess(product.getSeller().getUserId(), product.getProductId());
        // Người đặt giá hiện tại vượt qua người giữ giá cao nhất
      } else {
        previousHighestBidder = currentHighestBid.getBidder();
        newBid.setBidder(bidder);
        newBid.setProduct(product);
        newBid.setBidPrice(competitorMaxPrice.add(product.getPriceStep()));
        newBid.setMaxAutoPrice(bidderMaxPrice);

        messageType = MessageType.LEADING;
        message = bidder.getFullName() + " is now the highest bidder with bid of "
            + newBid.getBidPrice();

        emailProducer.sendBidSuccess(previousHighestBidder.getUserId(), product.getProductId());
        emailProducer.sendBidSuccess(bidder.getUserId(), product.getProductId());
        emailProducer.sendBidSuccess(product.getSeller().getUserId(), product.getProductId());
      }
    }

    // 3. Lưu bid mới
    Bid savedBid = _bidRepository.save(newBid);

    // 4. Cập nhật product
    product.setCurrentPrice(savedBid.getBidPrice());
    product.setHighestBidder(savedBid.getBidder());
    product.setBidCount(product.getBidCount() + 1);
    _productRepository.save(product);

    // 5. Broadcast update qua WebSocket
    _auctionService.broadcastAuctionUpdate(product, savedBid, previousPrice, messageType, message);

    log.info("[AUTO-BID] Completed: currentPrice={}, winner={}",
        savedBid.getBidPrice(), savedBid.getBidder().getFullName());

    return savedBid;
  }

  @Override
  public List<Bid> getTop5BidsByProductId(Integer productId) {
    return _bidRepository.findTop5ByProductProductIdOrderByBidPriceDescBidAtAsc(productId);
  }

  @Override
  public Bid getBid(Integer bidId) {
    return _bidRepository.findById(bidId)
        .orElseThrow(() -> new IllegalArgumentException("Bid not found"));
  }

  @Override
  @Transactional
  public void removeBidsByProductIdAndBidderId(Integer productId, Integer bidderId) {
    _bidRepository.deleteByProductProductIdAndBidderUserId(productId, bidderId);

    Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(productId);

    if (highestBid != null) {
      Product product = highestBid.getProduct();
      product.setHighestBidder(highestBid.getBidder());
      product.setCurrentPrice(highestBid.getBidPrice());
      _productRepository.save(product);
    }
  }
}
