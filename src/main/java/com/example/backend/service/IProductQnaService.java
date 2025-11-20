package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.ProductQnA.ProductQuestion;

public interface IProductQnaService {
    public List<ProductQuestion> getProductQnaByProductId(Integer productId);
}
