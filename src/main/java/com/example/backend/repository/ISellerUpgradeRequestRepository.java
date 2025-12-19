package com.example.backend.repository;

import com.example.backend.entity.SellerUpgradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISellerUpgradeRequestRepository extends JpaRepository<SellerUpgradeRequest, Integer> {

    List<SellerUpgradeRequest> findByStatus(SellerUpgradeRequest.Status status);

    boolean existsByUser_UserIdAndStatus(
            Integer userId,
            SellerUpgradeRequest.Status status);
}
