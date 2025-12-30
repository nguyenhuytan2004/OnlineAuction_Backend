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
    Role userRole;
    if (role != null) {
      try {
        userRole = Role.valueOf(role.toUpperCase());
        return _userRepository.findByRole(userRole, pageable);
      } catch (IllegalArgumentException e) {
        return _userRepository.findAll(pageable);
      }
    } else {
      return _userRepository.findAll(pageable);
    }
  }

  @Override
  public User getUser(Integer userId) {
    return _userRepository.findById(userId).orElse(null);
  }

  @Override
  public List<User> getAllUsers() {
    return _userRepository.findAll();
  }

  @Override
  public User updateUser(Integer userId, UpdateUserRequest updateUserRequest) {

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

    return _userRepository.save(user);
  }

  @Override
  public UserResponse createUser(CreateUserRequest request) {

    if (_userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email đã được sử dụng");
    }

    User user = new User();
    user.setFullName(request.getFullName());
    user.setEmail(request.getEmail());
    user.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
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

    return userResponse;
  }

  @Override
  public void deleteUser(Integer userId) {
    User user = _userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    _userRepository.delete(user);
  }

  @Override
  public User updateUserByAdmin(Integer userId, UpdateUserAdminRequest request) {

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
    return _userRepository.save(user);
  }

  @Override
  public void downgradeSeller(User seller) {
    // Kiểm tra xem seller còn sản phẩm đang đấu giá không
    List<Product> activeProducts = _productRepository.findBySeller_UserIdAndIsActiveTrue(seller.getUserId());
    if (!activeProducts.isEmpty()) {
      return;
    }

    // Kiểm tra xem seller còn sản phẩm đang trong quá trình thanh toán không
    List<AuctionOrder> pendingOrders = _auctionOrderRepository.findBySeller_UserIdAndStatusNotIn(seller.getUserId(),
        List.of(OrderStatus.COMPLETED, OrderStatus.CANCELED));
    if (!pendingOrders.isEmpty()) {
      return;
    }

    // Thực hiện hạ cấp seller xuống bidder
    seller.setRole(Role.BIDDER);
    seller.setSellerExpiresAt(null);
    _userRepository.save(seller);
    log.info("[SERVICE][USER] - Seller ID {} đã được hạ cấp xuống BIDDER.", seller.getUserId());
  }
}