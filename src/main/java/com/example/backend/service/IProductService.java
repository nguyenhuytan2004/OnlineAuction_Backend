package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.model.Product.CreateProductRequest;

public interface IProductService {
    public List<Product> getAllProducts();

    public Product getProductById(Integer productId);

    public Page<Product> getProductsByCategoryId(Integer categoryId, Pageable pageable);

    public List<Product> getTop5EndingSoonProducts();

    public List<Product> getTop5MostAuctionedProducts();

    public List<Product> getTop5HighestPricedProducts();

    public List<Product> getTop5RelatedProducts(Integer categoryId, Integer productId);

    public Page<Product> searchProducts(String keyword, Integer categoryId, Pageable pageable);

    Product createProduct(CreateProductRequest request, Integer sellerId);

    AuctionResult buyNowProduct(Integer productId, Integer buyerId);

    String appendDescription(Integer userId, Integer productId, String additionalDescription);
}
