package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer> {
    // Tìm tất cả sản phẩm có isActive = true
    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    List<Product> findTop5ByOrderByEndTimeAsc();

    List<Product> findTop5ByOrderByBidCountDesc();

    List<Product> findTop5ByOrderByCurrentPriceDesc();

    List<Product> findTop5ByCategoryCategoryIdAndProductIdNotOrderByEndTimeAsc(Integer categoryId, Integer productId);

    @Query("SELECT p FROM Product p " +
            "WHERE p.endTime < :now " +
            "AND p.isActive = true " +
            "AND p.productId NOT IN (SELECT ar.product.productId FROM AuctionResult ar) " +
            "ORDER BY p.endTime ASC")
    List<Product> findExpiredProductsWithoutResult(@Param("now") LocalDateTime now);

    List<Product> findBySellerUserIdAndEndTimeAfterOrderByEndTimeAsc(Integer userId, LocalDateTime now);
}
