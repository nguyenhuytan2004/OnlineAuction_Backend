package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;

public interface IBidRepository extends JpaRepository<Bid, Integer> {
    Bid findTopByProductProductIdOrderByBidPriceDesc(Integer productId);

    @Query("SELECT b.product FROM Bid b WHERE b.bidder.userId = :userId GROUP BY b.product.productId ORDER BY MAX(b.bidAt) DESC")
    List<Product> findDistinctProductsByBidderUserId(@Param("userId") Integer userId);

    List<Bid> findTop5ByProductProductIdOrderByBidPriceDescBidAtAsc(Integer productId);

    List<Bid> findByProductProductIdOrderByMaxAutoPriceDescBidAtAsc(Integer productId);

    Boolean existsByProductProductIdAndBidderUserId(Integer productId, Integer userId);

    boolean existsByProduct_ProductId(Integer productId);
}
