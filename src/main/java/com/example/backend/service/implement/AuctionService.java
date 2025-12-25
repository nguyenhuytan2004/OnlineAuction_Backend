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
  public void broadcastAuctionUpdate(Product product, Bid newBid, BigDecimal previousPrice,
      MessageType messageType, String message) {
    BidUpdateMessage bidUpdateMessage = BidUpdateMessage.builder()
        .productId(product.getProductId())
        .productName(product.getProductName())
        .currentPrice(product.getCurrentPrice())
        .previousPrice(previousPrice)
        .priceStep(product.getPriceStep())
        .highestBidderId(product.getHighestBidder() != null ? product.getHighestBidder().getUserId() : null)
        .highestBidderName(product.getHighestBidder() != null ? product.getHighestBidder().getFullName() : null)
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

    // Broadcast tới tất cả clients
    auctionMessagingTemplate.convertAndSend(
        "/topic/product/" + product.getProductId() + "/place-bid",
        bidUpdateMessage);

    log.info("[AUTO-BID] Broadcasted {} for product {}", messageType, product.getProductId());
  }

  @Override
  public void checkAndRenewAuction(Product product) {
    if (product.getIsAutoRenew()) {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime endTime = product.getEndTime();

      Duration triggerDuration = auctionProperties.getTriggerDuration();
      Duration extendDuration = auctionProperties.getExtendDuration();

      if (triggerDuration.compareTo(Duration.between(now, endTime)) >= 0) {
        product.setEndTime(endTime.plus(extendDuration));
        _productRepository.save(product);
      }

      auctionMessagingTemplate.convertAndSend(
          "/topic/product/" + product.getProductId() + "/auction-extend", product.getEndTime());

      log.info("[AUTO-BID] Auction extended for product {}", product.getProductId());
    }
  }

  @Override
  public void updateAuctionResult(Product product) {
    AuctionResult existingResult = _auctionResultRepository
        .findByProductProductId(product.getProductId());
    if (existingResult != null) {
      return;
    }

    Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(product.getProductId());
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
    }

    product.setIsActive(false);
    _productRepository.save(product);
  }

  @Override
  public void broadcastAuctionEnd(Product product, String reason) {
    auctionMessagingTemplate.convertAndSend(
        "/topic/product/" + product.getProductId() + "/auction-end", reason);
  }

  @Override
  public void broadcastQuestionAsked(ProductQuestion productQuestion) {
    auctionMessagingTemplate.convertAndSend(
        "/topic/products/" + productQuestion.getProduct().getProductId() + "/questions", productQuestion);
  }

  @Override
  public void broadcastAnswerPosted(ProductAnswer productAnswer, Integer productId) {
    auctionMessagingTemplate.convertAndSend(
        "/topic/products/" + productId + "/questions/" + productAnswer.getQuestion().getQuestionId()
            + "/answers",
        productAnswer);
  }

  @Override
  public void broadcastBidderBlocked(Integer blockedId, String reason) {
    auctionMessagingTemplate.convertAndSend(
        "/user/" + blockedId + "/queue/blocked-notification",
        reason);
  }
}
