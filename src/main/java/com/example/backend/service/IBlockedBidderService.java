package com.example.backend.service;

public interface IBlockedBidderService {
    Boolean checkBidderBlocked(Integer productId, Integer bidderId);
}
