package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Product;

public interface IProfileService {
    List<Product> getParticipatingProducts(Integer userId);

    List<Product> getWonProducts(Integer userId);
}