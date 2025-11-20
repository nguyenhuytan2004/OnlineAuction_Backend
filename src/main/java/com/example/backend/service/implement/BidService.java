package com.example.backend.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Bid;
import com.example.backend.entity.User;
import com.example.backend.repository.IBidRepository;
import com.example.backend.service.IBidService;

@Service
public class BidService implements IBidService {

    @Autowired
    private IBidRepository _bidRepository;

    @Override
    public User getHighestBidderByProductId(Integer productId) {
        Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(productId);

        if (highestBid == null) {
            return null;
        }

        Bid bid = highestBid;
        User bidder = bid.getBidder();

        return bidder;
    }
}
