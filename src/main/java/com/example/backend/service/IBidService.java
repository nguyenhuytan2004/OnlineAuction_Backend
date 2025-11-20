package com.example.backend.service;

import com.example.backend.entity.User;

public interface IBidService {
    User getHighestBidderByProductId(Integer productId);
}
