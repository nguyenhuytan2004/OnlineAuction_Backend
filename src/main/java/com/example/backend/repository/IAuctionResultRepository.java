package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;

public interface IAuctionResultRepository extends JpaRepository<AuctionResult, Integer> {
    AuctionResult findByProductProductId(Integer productId);

    @Query("SELECT ar.product FROM AuctionResult ar WHERE ar.winner.userId = :userId")
    List<Product> findWonProductsByWinnerUserId(@Param("userId") Integer userId);

    @Query("SELECT ar.product FROM AuctionResult ar WHERE ar.product.seller.userId = :userId")
    List<Product> findSoldProductsBySellerUserId(@Param("userId") Integer userId);

    boolean existsByProduct_ProductId(Integer productId);
}
