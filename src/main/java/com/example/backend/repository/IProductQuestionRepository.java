package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.ProductQnA.ProductQuestion;

public interface IProductQuestionRepository extends JpaRepository<ProductQuestion, Integer> {
    List<ProductQuestion> findByProductProductId(Integer productId);
}
