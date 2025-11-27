package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Product;

public interface ISellerProfileService {
    public List<Product> getActiveProducts(Integer userId);

    public List<Product> getSoldProducts(Integer userId);
}