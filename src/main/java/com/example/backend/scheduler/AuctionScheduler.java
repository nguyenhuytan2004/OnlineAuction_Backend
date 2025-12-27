package com.example.backend.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.entity.User.Role;
import com.example.backend.model.Email.EmailNotificationRequest.EmailType;
import com.example.backend.producer.EmailProducer;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IAuctionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuctionScheduler {

  @Autowired
  private IProductRepository _productRepository;

  @Autowired
  private IUserRepository _userRepository;

  @Autowired
  private IAuctionService _auctionService;
  @Autowired
  private EmailProducer emailProducer;

  @Scheduled(fixedDelay = 10000)
  @Transactional
  public void processExpiredAuctions() {
    try {
      List<Product> expiredProducts = _productRepository.findExpiredProductsWithoutResult(LocalDateTime.now());

      if (expiredProducts.isEmpty()) {
        return;
      }

      for (Product product : expiredProducts) {
        try {
          _auctionService.updateAuctionResult(product);
          _auctionService.broadcastAuctionEnd(product, "Phiên đấu giá đã kết thúc.");

          if (product.getHighestBidder() != null) {
            emailProducer.sendProductEmail(EmailType.AUCTION_ENDED_WINNER,
                product.getHighestBidder().getUserId(),
                product.getProductId());
            emailProducer.sendProductEmail(EmailType.AUCTION_ENDED_SELLER,
                product.getSeller().getUserId(),
                product.getProductId());
          } else {
            emailProducer.sendProductEmail(EmailType.AUCTION_ENDED_NO_WINNER_SELLER,
                product.getSeller().getUserId(),
                product.getProductId());
          }
        } catch (Exception e) {
          log.error("[SCHEDULER][AUCTION] Error processing product ID {}: {}", product.getProductId(),
              e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      log.error("[SCHEDULER][AUCTION] Error in process expired auctions: {}", e.getMessage(), e);
    }
  }

  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void processExpiredSellers() {
    try {
      List<User> expiredSellers = _userRepository.findExpiredSellers(LocalDateTime.now());

      if (expiredSellers.isEmpty()) {
        return;
      }

      for (User seller : expiredSellers) {
        try {
          seller.setRole(Role.BIDDER);
          seller.setSellerExpiresAt(null);
          _userRepository.save(seller);
        } catch (Exception e) {
          log.error("[SCHEDULER][SELLER_EXPIRY] Error processing user ID {}: {}", seller.getUserId(),
              e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      log.error("[SCHEDULER][SELLER_EXPIRY] Error in process expired sellers: {}", e.getMessage(), e);
    }
  }

}
