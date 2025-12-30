package com.example.backend.service.implement;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.AuctionOrder.OrderStatus;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.entity.User.Role;
import com.example.backend.model.User.CreateUserRequest;
import com.example.backend.model.User.UpdateUserAdminRequest;
import com.example.backend.model.User.UpdateUserRequest;
import com.example.backend.model.User.UserResponse;
import com.example.backend.repository.IAuctionOrderRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

  @Autowired
  private IUserRepository _userRepository;
  @Autowired
  private IProductRepository _productRepository;
  @Autowired
  private IAuctionOrderRepository _auctionOrderRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public Page<User> getUsers(String role, Pageable pageable) {

    log.info(
        "[SERVICE][GET][USERS] Input role={}, pageable={}",
        role,
        pageable);

    try {
      Page<User> result;
      if (role != null) {
        try {
          Role userRole = Role.valueOf(role.toUpperCase());
          result = _userRepository.findByRole(userRole, pageable);
        } catch (IllegalArgumentException e) {
          result = _userRepository.findAll(pageable);
        }
      } else {
        result = _userRepository.findAll(pageable);
      }

      log.info(
          "[SERVICE][GET][USERS] Output users={}",
          result.getContent());
      return result;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][USERS] Error occurred (role={}): {}",
          role,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public User getUser(Integer userId) {

    log.info(
        "[SERVICE][GET][USER] Input userId={}",
        userId);

    try {
      User user = _userRepository.findById(userId).orElse(null);

      log.info(
          "[SERVICE][GET][USER] Output user={}",
          user);
      return user;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][USER] Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public List<User> getAllUsers() {

    log.info("[SERVICE][GET][ALL_USERS] Input");

    try {
      List<User> users = _userRepository.findAll();

      log.info(
          "[SERVICE][GET][ALL_USERS] Output users={}",
          users);
      return users;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][ALL_USERS] Error occurred: {}",
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public User updateUser(Integer userId, UpdateUserRequest updateUserRequest) {

    log.info(
        "[SERVICE][PUT][UPDATE_USER] Input userId={}, request={}",
        userId,
        updateUserRequest);

    try {
      User user = _userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

      if (!user.getEmail().equals(updateUserRequest.getEmail())) {
        if (_userRepository.findByEmail(updateUserRequest.getEmail()).isPresent()) {
          throw new IllegalArgumentException("Email already in use");
        }
        user.setEmail(updateUserRequest.getEmail());
      }

      user.setFullName(updateUserRequest.getFullName());
      user.setRatingScore(updateUserRequest.getRatingScore());
      user.setRatingCount(updateUserRequest.getRatingCount());
      user.setRole(updateUserRequest.getRole());

      if (updateUserRequest.getSellerExpiresAt() != null)
        user.setSellerExpiresAt(updateUserRequest.getSellerExpiresAt());

      user.setIsActive(updateUserRequest.getIsActive());

      User saved = _userRepository.save(user);

      log.info(
          "[SERVICE][PUT][UPDATE_USER] Success userId={}",
          userId);

      return saved;

    } catch (Exception e) {
      log.error(
          "[SERVICE][PUT][UPDATE_USER] Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public UserResponse createUser(CreateUserRequest request) {

    log.info(
        "[SERVICE][POST][CREATE_USER] Input request={}",
        request);

    try {
      if (_userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email đã được sử dụng");
      }

      User user = new User();
      user.setFullName(request.getFullName());
      user.setEmail(request.getEmail());
      user.setEncryptedPassword(
          passwordEncoder.encode(request.getPassword()));
      user.setRatingScore(0);
      user.setRatingCount(0);
      user.setRole(request.getRole());

      if (request.getRole() == Role.SELLER) {
        user.setSellerExpiresAt(LocalDateTime.now().plusDays(7));
      }

      user.setIsActive(true);
      user.setIsVerified(true);

      User savedUser = _userRepository.save(user);
      UserResponse userResponse = new UserResponse(savedUser);

      log.info(
          "[SERVICE][POST][CREATE_USER] Success userId={}",
          savedUser.getUserId());

      return userResponse;

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][CREATE_USER] Error occurred (request={}): {}",
          request,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void deleteUser(Integer userId) {

    log.info(
        "[SERVICE][DELETE][USER] Input userId={}",
        userId);

    try {
      User user = _userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found"));

      _userRepository.delete(user);

      log.info(
          "[SERVICE][DELETE][USER] Success userId={}",
          userId);

    } catch (Exception e) {
      log.error(
          "[SERVICE][DELETE][USER] Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public User updateUserByAdmin(Integer userId, UpdateUserAdminRequest request) {

    log.info(
        "[SERVICE][PUT][UPDATE_USER_ADMIN] Input userId={}, request={}",
        userId,
        request);

    try {
      User user = _userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found"));

      if (!user.getEmail().equals(request.getEmail())) {
        if (_userRepository.findByEmail(request.getEmail()).isPresent()) {
          throw new IllegalArgumentException("Email already in use");
        }
        user.setEmail(request.getEmail());
      }

      user.setFullName(request.getFullName());

      if (request.getRole() != null) {
        user.setRole(request.getRole());
      }

      if (request.getIsActive() != null) {
        user.setIsActive(request.getIsActive());
      }

      User saved = _userRepository.save(user);

      log.info(
          "[SERVICE][PUT][UPDATE_USER_ADMIN] Success userId={}",
          userId);

      return saved;

    } catch (Exception e) {
      log.error(
          "[SERVICE][PUT][UPDATE_USER_ADMIN] Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void downgradeSeller(User seller) {

    log.info(
        "[SERVICE][POST][DOWNGRADE_SELLER] Input sellerId={}",
        seller.getUserId());

    try {
      // Kiểm tra xem seller còn sản phẩm đang đấu giá không
      List<Product> activeProducts = _productRepository
          .findBySeller_UserIdAndIsActiveTrue(
              seller.getUserId());
      if (!activeProducts.isEmpty()) {
        log.info(
            "[SERVICE][POST][DOWNGRADE_SELLER] Skip - active products exist sellerId={}",
            seller.getUserId());
        return;
      }

      // Kiểm tra xem seller còn sản phẩm đang trong quá trình thanh toán không
      List<AuctionOrder> pendingOrders = _auctionOrderRepository
          .findBySeller_UserIdAndStatusNotIn(
              seller.getUserId(),
              List.of(
                  OrderStatus.COMPLETED,
                  OrderStatus.CANCELLED));
      if (!pendingOrders.isEmpty()) {
        log.info(
            "[SERVICE][POST][DOWNGRADE_SELLER] Skip - pending orders exist sellerId={}",
            seller.getUserId());
        return;
      }

      // Thực hiện hạ cấp seller xuống bidder
      seller.setRole(Role.BIDDER);
      seller.setSellerExpiresAt(null);
      _userRepository.save(seller);

      log.info(
          "[SERVICE][POST][DOWNGRADE_SELLER] Success sellerId={} downgraded to BIDDER",
          seller.getUserId());

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][DOWNGRADE_SELLER] Error occurred (sellerId={}): {}",
          seller.getUserId(),
          e.getMessage(),
          e);
      throw e;
    }
  }
}
