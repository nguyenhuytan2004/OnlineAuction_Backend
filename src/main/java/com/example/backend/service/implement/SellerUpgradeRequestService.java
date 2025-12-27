package com.example.backend.service.implement;

import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.entity.User;
import com.example.backend.model.SellerUpgradeRequest.ReviewSellerUpgradeRequest;
import com.example.backend.repository.ISellerUpgradeRequestRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.ISellerUpgradeRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != User.Role.BIDDER) {
            throw new IllegalStateException("Only bidder can request upgrade");
        }

        boolean existsPending =
                _sellerUpgradeRequestRepository
                        .existsByUserUserIdAndStatus(
                                userId,
                                SellerUpgradeRequest.Status.PENDING
                        );

        if (existsPending) {
            throw new IllegalStateException("Upgrade request already pending");
        }

        SellerUpgradeRequest req = new SellerUpgradeRequest();
        req.setUser(user);
        req.setStatus(SellerUpgradeRequest.Status.PENDING);

        return _sellerUpgradeRequestRepository.save(req);
    }


    @Override
    public List<SellerUpgradeRequest> getPendingRequests() {
        return _sellerUpgradeRequestRepository.findByStatus(SellerUpgradeRequest.Status.PENDING);
    }

    @Override
    @Transactional
    public SellerUpgradeRequest reviewRequest(
            Integer requestId,
            ReviewSellerUpgradeRequest review) {

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
            userRepository.save(user);
        }

        return _sellerUpgradeRequestRepository.save(req);
    }

    @Override
    public Optional<SellerUpgradeRequest> getLatestRequestByUser(Integer userId) {
        return _sellerUpgradeRequestRepository
                .findTopByUser_UserIdOrderByRequestAtDesc(userId);
    }
}
