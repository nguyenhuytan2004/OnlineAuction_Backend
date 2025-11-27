package com.example.backend.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IBidRepository;
import com.example.backend.service.IBidderProfileService;

@Service
public class BidderProfileService implements IBidderProfileService {
    @Autowired
    private IBidRepository _bidRepository;
    @Autowired
    private IAuctionResultRepository _auctionResultRepository;

    @Override
    public List<Product> getParticipatingProducts(Integer userId) {
        return _bidRepository.findDistinctProductsByBidderUserId(userId);
    }

    @Override
    public List<Product> getWonProducts(Integer userId) {
        return _auctionResultRepository.findWonProductsByWinnerUserId(userId);
    }
}
