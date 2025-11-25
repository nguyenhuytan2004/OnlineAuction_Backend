package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.AuctionResult;

public interface IAuctionResultRepository extends JpaRepository<AuctionResult, Integer> {
    AuctionResult findByProductProductId(Integer productId);
}
