package com.example.backend.repository;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.AuctionOrder.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
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

  List<AuctionOrder> findBySeller_UserIdAndStatusNotIn(Integer sellerId, List<OrderStatus> status);
}
