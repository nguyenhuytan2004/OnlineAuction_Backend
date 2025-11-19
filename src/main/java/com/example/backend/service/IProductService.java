package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.entity.Product;

public interface IProductService {
    public Product getProduct(Integer productId);

    public Page<Product> getProductsByCategoryId(Integer categoryId, Pageable pageable);

    public List<Product> getTop5ProductsEndingSoon();

    public List<Product> getTop5ProductsByBidCount();

    public List<Product> getTop5ProductsByCurrentPrice();
}
