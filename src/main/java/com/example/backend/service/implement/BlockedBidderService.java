package com.example.backend.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.repository.IBlockedBidderRepository;
import com.example.backend.service.IBlockedBidderService;

@Service
public class BlockedBidderService implements IBlockedBidderService {
    @Autowired
    private IBlockedBidderRepository _blockedBidderRepository;

    @Override
    public Boolean checkBidderBlocked(Integer productId, Integer bidderId) {
        return _blockedBidderRepository.existsByProductProductIdAndBlockedUserId(productId, bidderId);
    }
}
