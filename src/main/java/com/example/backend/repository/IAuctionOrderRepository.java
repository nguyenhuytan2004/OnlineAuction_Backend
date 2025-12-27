package com.example.backend.repository;

import com.example.backend.entity.AuctionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IAuctionOrderRepository extends JpaRepository<AuctionOrder, Integer> {

  Optional<AuctionOrder> findByProductProductId(Integer productId);

  @Query("""
          SELECT
          (SUM(CASE WHEN o.status = 'PAID' THEN 1 ELSE 0 END) * 100)
          / COUNT(o)
          FROM AuctionOrder o
      """)
  Integer paymentSuccessRate();
}
