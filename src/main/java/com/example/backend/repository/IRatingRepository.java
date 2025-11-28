package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Rating;

public interface IRatingRepository extends JpaRepository<Rating, Integer> {
        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
                        "FROM Rating r " +
                        "WHERE r.reviewer.userId = :reviewerId " +
                        "AND r.reviewee.userId = :revieweeId " +
                        "AND r.product.productId = :productId")
        boolean existsByReviewerAndRevieweeAndProduct(
                        @Param("reviewerId") Integer reviewerId,
                        @Param("revieweeId") Integer revieweeId,
                        @Param("productId") Integer productId);

        Rating findByProductProductIdAndReviewerUserIdAndRevieweeUserId(
                        Integer productId,
                        Integer reviewerId,
                        Integer revieweeId);
}
