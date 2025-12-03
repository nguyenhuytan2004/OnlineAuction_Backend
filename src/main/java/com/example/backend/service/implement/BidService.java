package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.config.AuctionProperties;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IBidService;
import com.example.backend.service.IProductService;

@Service
public class BidService implements IBidService {

    @Autowired
    private IBidRepository _bidRepository;
    @Autowired
    private IProductRepository _productRepository;
    @Autowired
    private IUserRepository _userRepository;

    @Autowired
    private IProductService _productService;

    @Autowired
    private AuctionProperties auctionProperties;

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

    @Override
    public Bid placeBid(CreateBidRequest createBidRequest) throws Exception {
        Product product = _productRepository.findById(createBidRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        User bidder = _userRepository.findById(createBidRequest.getBidderId())
                .orElseThrow(() -> new IllegalArgumentException("Bidder not found"));

        BigDecimal minBidPrice = product.getCurrentPrice().add(product.getPriceStep());
        if (createBidRequest.getBidPrice().compareTo(minBidPrice) < 0) {
            throw new IllegalArgumentException("Bid price must be at least " + minBidPrice);
        }

        if (LocalDateTime.now().isAfter(product.getEndTime())) {
            throw new IllegalArgumentException("Auction has already ended");
        }

        Boolean isEligible = _productService.checkBiddingEligibility(product.getProductId(), bidder.getUserId());
        if (!isEligible) {
            throw new IllegalArgumentException("Bidder is not eligible to place a bid on this product");
        }

        Bid newBid = new Bid();
        newBid.setProduct(product);
        newBid.setBidder(bidder);
        newBid.setBidPrice(createBidRequest.getBidPrice());
        newBid.setMaxAutoPrice(createBidRequest.getMaxAutoPrice());

        Bid savedBid = _bidRepository.save(newBid);

        product.setCurrentPrice(createBidRequest.getBidPrice());
        product.setBidCount(product.getBidCount() + 1);
        _productRepository.save(product);

        checkAndRenewAuction(product);

        return savedBid;
    }

    // Extra method to check and renew auction
    @Override
    public void checkAndRenewAuction(Product product) {
        if (product.getIsAutoRenew()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = product.getEndTime();

            Duration triggerDuration = auctionProperties.getTriggerDuration();
            Duration extendDuration = auctionProperties.getExtendDuration();

            if (triggerDuration.compareTo(Duration.between(now, endTime)) >= 0) {
                product.setEndTime(endTime.plus(extendDuration));
                _productRepository.save(product);
            }
        }
    }

    @Override
    public List<Bid> getTop5BidsByProductId(Integer productId) {
        return _bidRepository.findTop5ByProductProductIdOrderByBidPriceDescBidAtAsc(productId);
    }
}
