package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.ProductQnA.ProductAnswer;

public interface IProductAnswerRepository extends JpaRepository<ProductAnswer, Integer> {
}
