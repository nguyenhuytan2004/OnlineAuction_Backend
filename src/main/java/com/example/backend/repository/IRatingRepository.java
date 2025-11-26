package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Rating;

public interface IRatingRepository extends JpaRepository<Rating, Integer> {
    // Kiểm tra xem bidder đã đánh giá seller cho product chưa vì mỗi bidder chỉ
    // được đánh giá seller 1 lần cho 1 sản phẩm
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Rating r " +
            "WHERE r.winner.userId = :winnerId " +
            "AND r.seller.userId = :sellerId " +
            "AND r.product.productId = :productId")
    boolean existsByBidderAndSellerAndProduct(
            @Param("winnerId") Integer winnerId,
            @Param("sellerId") Integer sellerId,
            @Param("productId") Integer productId);
}
