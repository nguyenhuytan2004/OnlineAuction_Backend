package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.entity.Product;

public interface IProductService {
    public List<Product> getAllProducts();

    public Product getProductById(Integer productId);

    public Page<Product> getProductsByCategoryId(Integer categoryId, Pageable pageable);

    public List<Product> getTop5EndingSoonProducts();

    public List<Product> getTop5MostAuctionedProducts();

    public List<Product> getTop5HighestPricedProducts();

    public List<Product> getTop5RelatedProducts(Integer categoryId, Integer productId);
}
