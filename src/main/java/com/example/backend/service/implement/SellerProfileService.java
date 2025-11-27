package com.example.backend.service.implement;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.ISellerProfileService;

@Service
public class SellerProfileService implements ISellerProfileService {
    @Autowired
    private IProductRepository _productRepository;

    @Override
    public List<Product> getActiveProducts(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        return _productRepository.findBySellerUserIdAndEndTimeAfterOrderByEndTimeAsc(userId, now);
    }
}
