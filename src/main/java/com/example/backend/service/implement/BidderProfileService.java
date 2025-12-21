package com.example.backend.service.implement;

import java.util.List;

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
    return _bidRepository.findDistinctProductsByBidderUserIdAndProductIsActiveTrue(userId);
  }

  @Override
  public List<Product> getWonProducts(Integer userId) {
    return _auctionResultRepository.findWonProductsByWinnerUserId(userId);
  }

  @Override
  public void changePassword(Integer userId, String currentPassword,
      String newPassword, String confirmNewPassword) {
    if (!newPassword.equals(confirmNewPassword)) {
      throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu mới không khớp");
    }

    User user = _userRepository.findById(userId)
        .orElse(null);

    if (!passwordEncoder.matches(currentPassword, user.getEncryptedPassword())) {
      throw new RuntimeException("Mật khẩu hiện tại không đúng");
    }

    user.setEncryptedPassword(passwordEncoder.encode(newPassword));
    _userRepository.save(user);
  }
}
