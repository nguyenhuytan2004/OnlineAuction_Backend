package com.example.backend.service;

import com.example.backend.entity.AuctionResult;

public interface IAuctionResultService {
  AuctionResult getAuctionResult(Integer productId);

  AuctionResult cancelAuction(Integer productId, Integer userId);
}
