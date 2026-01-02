package com.example.backend.service.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.entity.User;
import com.example.backend.model.SellerUpgradeRequest.ReviewSellerUpgradeRequest;
import com.example.backend.repository.ISellerUpgradeRequestRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.ISellerUpgradeRequestService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerUpgradeRequestService
    implements ISellerUpgradeRequestService {

  private final ISellerUpgradeRequestRepository _sellerUpgradeRequestRepository;
  private final IUserRepository userRepository;

  @Override
  @Transactional
  public SellerUpgradeRequest createRequest(Integer userId) {

    log.info(
        "[SERVICE][POST][SELLER_UPGRADE_REQUEST] Input userId={}",
        userId);

    try {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found"));

      if (user.getRole() == User.Role.SELLER && user.getSellerExpiresAt() != null
          && user.getSellerExpiresAt().isAfter(LocalDateTime.now())) {
        throw new IllegalStateException("Only bidder can request upgrade");
      }

      boolean existsPending = _sellerUpgradeRequestRepository
          .existsByUserUserIdAndStatus(
              userId,
              SellerUpgradeRequest.Status.PENDING);

      if (existsPending) {
        throw new IllegalStateException("Upgrade request already pending");
      }

      SellerUpgradeRequest req = new SellerUpgradeRequest();
      req.setUser(user);
      req.setStatus(SellerUpgradeRequest.Status.PENDING);

      SellerUpgradeRequest saved = _sellerUpgradeRequestRepository.save(req);

      log.info(
          "[SERVICE][POST][SELLER_UPGRADE_REQUEST] Success requestId={}, userId={}",
          saved.getRequestId(),
          userId);

      return saved;

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][SELLER_UPGRADE_REQUEST] Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public List<SellerUpgradeRequest> getPendingRequests() {

    log.info(
        "[SERVICE][GET][SELLER_UPGRADE_REQUESTS_PENDING] Input");

    try {
      List<SellerUpgradeRequest> requests = _sellerUpgradeRequestRepository
          .findByStatus(SellerUpgradeRequest.Status.PENDING);

      log.info(
          "[SERVICE][GET][SELLER_UPGRADE_REQUESTS_PENDING] Output requests={}",
          requests);

      return requests;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][SELLER_UPGRADE_REQUESTS_PENDING] Error occurred: {}",
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public SellerUpgradeRequest reviewRequest(
      Integer requestId,
      ReviewSellerUpgradeRequest review) {

    log.info(
        "[SERVICE][POST][REVIEW_SELLER_UPGRADE_REQUEST] Input requestId={}, review={}",
        requestId,
        review);

    try {
      SellerUpgradeRequest req = _sellerUpgradeRequestRepository.findById(requestId)
          .orElseThrow(() -> new IllegalArgumentException("Request not found"));

      if (req.getStatus() != SellerUpgradeRequest.Status.PENDING) {
        throw new IllegalStateException("Request already reviewed");
      }

      req.setStatus(review.getStatus());
      req.setReviewedAt(LocalDateTime.now());
      req.setComments(review.getComments());

      if (review.getStatus() == SellerUpgradeRequest.Status.APPROVED) {
        User user = req.getUser();
        user.setRole(User.Role.SELLER);
        user.setSellerExpiresAt(LocalDateTime.now().plusDays(7));
      }

      SellerUpgradeRequest saved = _sellerUpgradeRequestRepository.save(req);

      log.info(
          "[SERVICE][POST][REVIEW_SELLER_UPGRADE_REQUEST] Success requestId={}, status={}",
          requestId,
          saved.getStatus());

      return saved;

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][REVIEW_SELLER_UPGRADE_REQUEST] Error occurred (requestId={}): {}",
          requestId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public Optional<SellerUpgradeRequest> getLatestRequestByUser(Integer userId) {

    log.info(
        "[SERVICE][GET][LATEST_SELLER_UPGRADE_REQUEST] Input userId={}",
        userId);

    try {
      Optional<SellerUpgradeRequest> result = _sellerUpgradeRequestRepository
          .findTopByUser_UserIdOrderByRequestAtDesc(userId);

      log.info(
          "[SERVICE][GET][LATEST_SELLER_UPGRADE_REQUEST] Output request={}",
          result.orElse(null));

      return result;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][LATEST_SELLER_UPGRADE_REQUEST] Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      throw e;
    }
  }
}
