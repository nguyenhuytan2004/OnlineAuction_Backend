package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.WatchList;
import org.springframework.data.jpa.repository.Query;

public interface IWatchListRepository extends JpaRepository<WatchList, Integer> {
    List<WatchList> findByUserUserId(Integer userId);

    WatchList findByUserUserIdAndProductProductId(Integer userId, Integer productId);

    @Query("""
        SELECT p.productName
        FROM WatchList w
        JOIN w.product p
        GROUP BY p.productId
        ORDER BY COUNT(w) DESC
    """)
    List<String> findTopProduct();
}
