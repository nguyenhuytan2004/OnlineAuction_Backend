package com.example.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    List<Product> findTop5ByOrderByEndTimeAsc();

    List<Product> findTop5ByOrderByBidCountDesc();

    List<Product> findTop5ByOrderByCurrentPriceDesc();

    List<Product> findTop5ByCategoryCategoryIdAndProductIdNotOrderByEndTimeAsc(Integer categoryId, Integer productId);
}
