package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Bid;

public interface IBidRepository extends JpaRepository<Bid, Integer> {
    Bid findTopByProductProductIdOrderByBidPriceDesc(Integer productId);
}
