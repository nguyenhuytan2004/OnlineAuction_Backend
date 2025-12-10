package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.BlockedBidder;

public interface IBlockedBidderRepository extends JpaRepository<BlockedBidder, Integer> {
    Boolean existsByProductProductIdAndBlockedUserId(Integer productId, Integer bidderId);
}
