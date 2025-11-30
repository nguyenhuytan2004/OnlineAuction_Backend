package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.model.Product.CreateProductRequest;

public interface IProductService {
    public Page<Product> getAllProducts(Pageable pageable);

    public Product getProductById(Integer productId);

    public Page<Product> getProductsByCategoryId(Integer categoryId, Pageable pageable);

    public List<Product> getTop5EndingSoonProducts();

    public List<Product> getTop5MostAuctionedProducts();

    public List<Product> getTop5HighestPricedProducts();

    public List<Product> getTop5RelatedProducts(Integer categoryId, Integer productId);

    public Page<Product> searchProducts(String keyword, Integer categoryId, Pageable pageable);

    public Product createProduct(CreateProductRequest request, Integer sellerId);

    public AuctionResult buyNowProduct(Integer productId, Integer buyerId);

    public String appendDescription(Integer userId, Integer productId, String additionalDescription);

    public Boolean checkBiddingEligibility(Integer productId, Integer userId);
}
