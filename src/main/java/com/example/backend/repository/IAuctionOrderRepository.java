package com.example.backend.repository;

import com.example.backend.entity.AuctionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAuctionOrderRepository extends JpaRepository<AuctionOrder, Integer> {

    Optional<AuctionOrder> findByProductId(Integer productId);
}
