package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Bid;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;

public interface IBidService {
    User getHighestBidderByProductId(Integer productId);

    Bid placeBid(CreateBidRequest createBidRequest) throws Exception;

    List<Bid> getTop5BidsByProductId(Integer productId);
}
