package com.example.backend.service.implement;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IBidderProfileService;

@Service
@Slf4j
public class BidderProfileService implements IBidderProfileService {

  @Autowired
  private IBidRepository _bidRepository;
  @Autowired
  private IAuctionResultRepository _auctionResultRepository;
  @Autowired
  private IUserRepository _userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public List<Product> getParticipatingProducts(Integer userId) {
    log.info(
            "[SERVICE][GET][BIDDER_PARTICIPATING_PRODUCTS] Input userId={}",
            userId
    );

    try {
      List<Product> products =
              _bidRepository.findDistinctProductsByBidderUserIdAndProductIsActiveTrue(userId);

      log.info(
              "[SERVICE][GET][BIDDER_PARTICIPATING_PRODUCTS] Output products={}",
              products
      );
      return products;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][BIDDER_PARTICIPATING_PRODUCTS] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public List<Product> getWonProducts(Integer userId) {
    log.info(
            "[SERVICE][GET][BIDDER_WON_PRODUCTS] Input userId={}",
            userId
    );

    try {
      List<Product> products =
              _auctionResultRepository.findWonProductsByWinnerUserId(userId);

      log.info(
              "[SERVICE][GET][BIDDER_WON_PRODUCTS] Output products={}",
              products
      );
      return products;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][BIDDER_WON_PRODUCTS] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public void changePassword(
          Integer userId,
          String currentPassword,
          String newPassword,
          String confirmNewPassword) {

    log.info(
            "[SERVICE][POST][CHANGE_PASSWORD] Input userId={}",
            userId
    );

    try {
      if (!newPassword.equals(confirmNewPassword)) {
        throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu mới không khớp");
      }

      User user = _userRepository.findById(userId)
              .orElseThrow(() -> new RuntimeException("User not found"));

      if (!passwordEncoder.matches(currentPassword, user.getEncryptedPassword())) {
        throw new RuntimeException("Mật khẩu hiện tại không đúng");
      }

      user.setEncryptedPassword(passwordEncoder.encode(newPassword));
      _userRepository.save(user);

      log.info(
              "[SERVICE][POST][CHANGE_PASSWORD] Success userId={}",
              userId
      );

    } catch (Exception e) {
      log.error(
              "[SERVICE][POST][CHANGE_PASSWORD] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
