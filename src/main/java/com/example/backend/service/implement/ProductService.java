package com.example.backend.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IProductService;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository _productRepository;

    @Override
    public Product getProduct(Integer productId) {
        return _productRepository.findById(productId).orElse(null);
    }

    @Override
    public Page<Product> getProductsByCategoryId(Integer categoryId, Pageable pageable) {
        return _productRepository.findByCategoryCategoryId(categoryId, pageable);
    }

    @Override
    public List<Product> getTop5EndingSoonProducts() {
        return _productRepository.findTop5ByOrderByEndTimeAsc();
    }

    @Override
    public List<Product> getTop5MostAuctionedProducts() {
        return _productRepository.findTop5ByOrderByBidCountDesc();
    }

    @Override
    public List<Product> getTop5HighestPricedProducts() {
        return _productRepository.findTop5ByOrderByCurrentPriceDesc();
    }
}
