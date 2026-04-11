package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.backend.config.AuctionProperties;
import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Conversation;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductQnA.ProductAnswer;
import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.model.WebSocket.BidUpdateMessage;
import com.example.backend.model.WebSocket.BidUpdateMessage.MessageType;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IConversationRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IAuctionService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuctionService implements IAuctionService {

  @Autowired
  private IProductRepository _productRepository;
  @Autowired
  private SimpMessagingTemplate auctionMessagingTemplate;
  @Autowired
  private AuctionProperties auctionProperties;
  @Autowired
  private IAuctionResultRepository _auctionResultRepository;
  @Autowired
  private IBidRepository _bidRepository;
  @Autowired
  private IConversationRepository _conversationRepository;

  @Override
  public void broadcastAuctionUpdate(
      Product product,
      Bid newBid,
      BigDecimal previousPrice,
      MessageType messageType,
      String message) {

    log.info(
        "[SERVICE][BROADCAST][AUCTION_UPDATE] Input productId={}, bidId={}, messageType={}",
        product.getProductId(),
        newBid.getBidId(),
        messageType);

    try {
      BidUpdateMessage bidUpdateMessage = BidUpdateMessage.builder()
          .productId(product.getProductId())
          .productName(product.getProductName())
          .currentPrice(product.getCurrentPrice())
          .previousPrice(previousPrice)
          .priceStep(product.getPriceStep())
          .highestBidderId(
              product.getHighestBidder() != null
                  ? product.getHighestBidder().getUserId()
                  : null)
          .highestBidderName(
              product.getHighestBidder() != null
                  ? product.getHighestBidder().getFullName()
                  : null)
          .newBidId(newBid.getBidId())
          .newBidderId(newBid.getBidder().getUserId())
          .newBidderName(newBid.getBidder().getFullName())
          .newBidPrice(newBid.getBidPrice())
          .newBidMaxPrice(newBid.getMaxAutoPrice())
          .bidAt(newBid.getBidAt())
          .totalBids(product.getBidCount())
          .messageType(messageType)
          .message(message)
          .build();

      auctionMessagingTemplate.convertAndSend(
          "/topic/product/" + product.getProductId() + "/place-bid",
          bidUpdateMessage);

      log.info(
          "[SERVICE][BROADCAST][AUCTION_UPDATE] Success productId={}, currentPrice={}",
          product.getProductId(),
          product.getCurrentPrice());

    } catch (Exception e) {
      log.error(
          "[SERVICE][BROADCAST][AUCTION_UPDATE] Error occurred (productId={}): {}",
          product.getProductId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void checkAndRenewAuction(Product product) {
    log.info(
        "[SERVICE][CHECK][AUCTION_RENEW] Input productId={}, autoRenew={}",
        product.getProductId(),
        product.getIsAutoRenew());

    try {
      if (product.getIsAutoRenew()) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = product.getEndTime();

        Duration triggerDuration = auctionProperties.getTriggerDuration();
        Duration extendDuration = auctionProperties.getExtendDuration();

        if (triggerDuration.compareTo(Duration.between(now, endTime)) >= 0) {
          product.setEndTime(endTime.plus(extendDuration));
          _productRepository.save(product);

          auctionMessagingTemplate.convertAndSend(
              "/topic/product/" + product.getProductId() + "/auction-extend",
              product.getEndTime());

          log.info(
              "[SERVICE][CHECK][AUCTION_RENEW] Extended productId={}, newEndTime={}",
              product.getProductId(),
              product.getEndTime());
        }
      }
    } catch (Exception e) {
      log.error(
          "[SERVICE][CHECK][AUCTION_RENEW] Error occurred (productId={}): {}",
          product.getProductId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public void updateAuctionResult(Product product) {
    log.info(
        "[SERVICE][UPDATE][AUCTION_RESULT] Input productId={}",
        product.getProductId());

    try {
      AuctionResult existingResult = _auctionResultRepository.findByProductProductId(product.getProductId());
      if (existingResult != null) {
        log.info(
            "[SERVICE][UPDATE][AUCTION_RESULT] Skip (already exists) productId={}",
            product.getProductId());
        return;
      }

      Bid highestBid = _bidRepository
          .findFirstByProductProductIdOrderByBidPriceDescBidAtAscBidIdAsc(product.getProductId());

      if (highestBid != null) {
        AuctionResult auctionResult = new AuctionResult();
        auctionResult.setProduct(product);
        auctionResult.setWinner(highestBid.getBidder());
        auctionResult.setFinalPrice(product.getCurrentPrice());
        auctionResult.setPaymentStatus(AuctionResult.PaymentStatus.PENDING);
        _auctionResultRepository.save(auctionResult);

        Conversation conversation = Conversation.builder()
            .product(product)
            .seller(product.getSeller())
            .buyer(highestBid.getBidder())
            .isActive(true)
            .build();
        _conversationRepository.save(conversation);

        log.info(
            "[SERVICE][UPDATE][AUCTION_RESULT] Created result productId={}, winnerId={}",
            product.getProductId(),
            highestBid.getBidder().getUserId());
      }

      product.setIsActive(false);
      _productRepository.save(product);

      log.info(
          "[SERVICE][UPDATE][AUCTION_RESULT] Auction closed productId={}",
          product.getProductId());

    } catch (Exception e) {
      log.error(
          "[SERVICE][UPDATE][AUCTION_RESULT] Error occurred (productId={}): {}",
          product.getProductId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void broadcastAuctionEnd(Product product, String reason) {
    log.info(
        "[SERVICE][BROADCAST][AUCTION_END] Input productId={}, reason={}",
        product.getProductId(),
        reason);

    try {
      auctionMessagingTemplate.convertAndSend(
          "/topic/product/" + product.getProductId() + "/auction-end",
          reason);

      log.info(
          "[SERVICE][BROADCAST][AUCTION_END] Success productId={}",
          product.getProductId());

    } catch (Exception e) {
      log.error(
          "[SERVICE][BROADCAST][AUCTION_END] Error occurred (productId={}): {}",
          product.getProductId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void broadcastQuestionAsked(ProductQuestion productQuestion) {
    log.info(
        "[SERVICE][BROADCAST][QUESTION_ASKED] Input productId={}, questionId={}",
        productQuestion.getProduct().getProductId(),
        productQuestion.getQuestionId());

    try {
      auctionMessagingTemplate.convertAndSend(
          "/topic/products/"
              + productQuestion.getProduct().getProductId()
              + "/questions",
          productQuestion);

      log.info(
          "[SERVICE][BROADCAST][QUESTION_ASKED] Success productId={}, questionId={}",
          productQuestion.getProduct().getProductId(),
          productQuestion.getQuestionId());

    } catch (Exception e) {
      log.error(
          "[SERVICE][BROADCAST][QUESTION_ASKED] Error occurred (productId={}): {}",
          productQuestion.getProduct().getProductId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void broadcastAnswerPosted(ProductAnswer productAnswer, Integer productId) {
    log.info(
        "[SERVICE][BROADCAST][ANSWER_POSTED] Input productId={}, answerId={}",
        productId,
        productAnswer.getAnswerId());

    try {
      auctionMessagingTemplate.convertAndSend(
          "/topic/products/"
              + productId
              + "/questions/"
              + productAnswer.getQuestion().getQuestionId()
              + "/answers",
          productAnswer);

      log.info(
          "[SERVICE][BROADCAST][ANSWER_POSTED] Success productId={}, answerId={}",
          productId,
          productAnswer.getAnswerId());

    } catch (Exception e) {
      log.error(
          "[SERVICE][BROADCAST][ANSWER_POSTED] Error occurred (productId={}): {}",
          productId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void broadcastBidderBlocked(Integer blockedId, String reason) {
    log.info(
        "[SERVICE][BROADCAST][BIDDER_BLOCKED] Input blockedId={}, reason={}",
        blockedId,
        reason);

    try {
      auctionMessagingTemplate.convertAndSend(
          "/user/" + blockedId + "/queue/blocked-notification",
          reason);

      log.info(
          "[SERVICE][BROADCAST][BIDDER_BLOCKED] Success blockedId={}",
          blockedId);

    } catch (Exception e) {
      log.error(
          "[SERVICE][BROADCAST][BIDDER_BLOCKED] Error occurred (blockedId={}): {}",
          blockedId,
          e.getMessage(),
          e);
      throw e;
    }
  }
}
