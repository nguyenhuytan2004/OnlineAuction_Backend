package com.example.backend.repository;

import java.math.BigDecimal;
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


    @Query("""
        SELECT COALESCE(SUM(ar.finalPrice), 0)
        FROM AuctionResult ar
        WHERE ar.paymentStatus = 'PAID'
    """)
    BigDecimal sumRevenue();

    @Query("""
        SELECT
        (SUM(CASE WHEN ar.paymentStatus = 'PAID' THEN 1 ELSE 0 END) * 100.0)
        / COUNT(ar)
        FROM AuctionResult ar
    """)
    Double successRate();

    @Query("""
    SELECT YEAR(ar.resultTime), MONTH(ar.resultTime), SUM(ar.finalPrice)
    FROM AuctionResult ar
    WHERE ar.paymentStatus = 'PAID'
    GROUP BY YEAR(ar.resultTime), MONTH(ar.resultTime)
    ORDER BY YEAR(ar.resultTime) DESC, MONTH(ar.resultTime) DESC
""")
    List<Object[]> revenueByMonth();

}
