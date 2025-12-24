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
        Page<Product> findByIsActiveTrue(Pageable pageable);

        Page<Product> findByIsActiveTrueAndCategoryCategoryId(Integer categoryId, Pageable pageable);

        List<Product> findTop5ByIsActiveTrueOrderByEndTimeAsc();

        List<Product> findTop5ByIsActiveTrueOrderByBidCountDesc();

        List<Product> findTop5ByIsActiveTrueOrderByCurrentPriceDesc();

        List<Product> findTop5ByIsActiveTrueAndCategoryCategoryIdAndProductIdNotOrderByEndTimeAsc(Integer categoryId,
                        Integer productId);

        @Query("SELECT p FROM Product p " +
                        "WHERE p.endTime < :now " +
                        "AND p.isActive = true " +
                        "AND p.productId NOT IN (SELECT ar.product.productId FROM AuctionResult ar) " +
                        "ORDER BY p.endTime ASC")
        List<Product> findExpiredProductsWithoutResult(@Param("now") LocalDateTime now);

        List<Product> findBySellerUserIdAndEndTimeAfterOrderByEndTimeAsc(Integer userId, LocalDateTime now);

        boolean existsByCategory_CategoryId(Integer categoryId);

        @Query("SELECT COUNT(p) FROM Product p")
        long countAll();

        @Query("""
            SELECT YEAR(p.createdAt), MONTH(p.createdAt), COUNT(p)
            FROM Product p
            GROUP BY YEAR(p.createdAt), MONTH(p.createdAt)
            ORDER BY YEAR(p.createdAt) DESC, MONTH(p.createdAt) DESC
        """)
        List<Object[]> countProductsByMonth();

        @Query("""
                SELECT COUNT(p)
                FROM Product p
                WHERE MONTH(p.createdAt) = MONTH(CURRENT_DATE)
                  AND YEAR(p.createdAt) = YEAR(CURRENT_DATE)
            """)
        long countThisMonth();
}
