package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.backend.config.AuctionProperties;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.model.WebSocket.BidUpdateMessage;
import com.example.backend.model.WebSocket.BidUpdateMessage.MessageType;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IBidService;
import com.example.backend.service.IProductService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @Autowired
    private SimpMessagingTemplate bidMessagingTemplate;

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

        if (LocalDateTime.now().isAfter(product.getEndTime())) {
            throw new IllegalArgumentException("Auction has already ended");
        }

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Product is not active");
        }

        Boolean isEligible = _productService.checkBiddingEligibility(product.getProductId(), bidder.getUserId());
        if (!isEligible) {
            throw new IllegalArgumentException("Bidder is not eligible to place a bid on this product");
        }

        // 4. Validate max bid amount
        BigDecimal minRequiredMaxBid = product.getCurrentPrice().add(product.getPriceStep());
        if (createBidRequest.getMaxAutoPrice().compareTo(minRequiredMaxBid) < 0) {
            throw new IllegalArgumentException("Max bid must be at least " + minRequiredMaxBid);
        }

        Bid savedBid = processAutoBid(product, bidder, createBidRequest);

        checkAndRenewAuction(product);

        return savedBid;
    }

    // Extra method to process auto-bid logic
    @Transactional
    private Bid processAutoBid(Product product, User bidder, CreateBidRequest request) {
        log.info("[AUTO-BID] Processing bid for product {} by user {} with maxPrice {}",
                product.getProductId(), bidder.getUserId(), request.getMaxAutoPrice());

        BigDecimal previousPrice = product.getCurrentPrice();

        // 1. Lấy bid có bidPrice cao nhất
        Bid currentHighestBid = _bidRepository
                .findTopByProductProductIdOrderByBidPriceDesc(product.getProductId());

        MessageType messageType;
        String message;
        Bid newBid = new Bid();

        // 2. Logic auto bid
        if (currentHighestBid == null) {
            newBid.setBidder(bidder);
            newBid.setProduct(product);
            newBid.setBidPrice(product.getCurrentPrice());
            newBid.setMaxAutoPrice(request.getMaxAutoPrice());

            messageType = MessageType.NEWBID;
            message = bidder.getFullName() + " placed the first bid of "
                    + newBid.getBidPrice();
        } else {
            if (currentHighestBid.getBidder().getUserId().equals(bidder.getUserId())) {
                currentHighestBid.setMaxAutoPrice(request.getMaxAutoPrice());
                Bid updatedBid = _bidRepository.save(currentHighestBid);

                log.info("[AUTO-BID] Updated existing highest bid for same bidder: new maxAutoPrice={}",
                        request.getMaxAutoPrice());

                return updatedBid;
            }

            BigDecimal competitorMaxPrice = currentHighestBid.getMaxAutoPrice();
            BigDecimal bidderMaxPrice = request.getMaxAutoPrice();
            if (bidderMaxPrice.compareTo(competitorMaxPrice) <= 0) {
                newBid.setBidder(currentHighestBid.getBidder());
                newBid.setProduct(product);
                newBid.setBidPrice(bidderMaxPrice);
                newBid.setMaxAutoPrice(competitorMaxPrice);

                messageType = MessageType.OUTBID;
                message = bidder.getFullName() + "was outbid by "
                        + currentHighestBid.getBidder().getFullName() + " with bid of "
                        + newBid.getBidPrice();
            } else {
                newBid.setBidder(bidder);
                newBid.setProduct(product);
                newBid.setBidPrice(competitorMaxPrice.add(product.getPriceStep()));
                newBid.setMaxAutoPrice(bidderMaxPrice);

                messageType = MessageType.LEADING;
                message = bidder.getFullName() + " is now the highest bidder with bid of "
                        + newBid.getBidPrice();
            }
        }

        // 3. Lưu bid mới
        Bid savedBid = _bidRepository.save(newBid);

        // 4. Cập nhật product
        product.setCurrentPrice(savedBid.getBidPrice());
        product.setHighestBidder(savedBid.getBidder());
        product.setBidCount(product.getBidCount() + 1);
        _productRepository.save(product);

        // 5. Broadcast update qua WebSocket
        broadcastBidUpdate(product, savedBid, previousPrice, messageType, message);

        log.info("[AUTO-BID] Completed: currentPrice={}, winner={}",
                savedBid.getBidPrice(), savedBid.getBidder().getFullName());

        return savedBid;
    }

    // Extra method to broadcast bid update via WebSocket
    private void broadcastBidUpdate(Product product, Bid newBid, BigDecimal previousPrice,
            MessageType messageType, String message) {
        BidUpdateMessage wsMessage = BidUpdateMessage.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .currentPrice(product.getCurrentPrice())
                .previousPrice(previousPrice)
                .priceStep(product.getPriceStep())
                .highestBidderId(product.getHighestBidder() != null ? product.getHighestBidder().getUserId() : null)
                .highestBidderName(product.getHighestBidder() != null ? product.getHighestBidder().getFullName() : null)
                .newBidId(newBid.getBidId())
                .newBidderId(newBid.getBidder().getUserId())
                .newBidderName(newBid.getBidder().getFullName())
                .newBidPrice(newBid.getBidPrice())
                .newBidMaxPrice(newBid.getMaxAutoPrice())
                .bidAt(newBid.getBidAt())
                .totalBids(product.getBidCount())
                .messageType(messageType)
                .message(message)
                .build();

        // Broadcast tới tất cả clients
        bidMessagingTemplate.convertAndSend(
                "/topic/product/" + product.getProductId() + "/place-bid",
                wsMessage);

        log.info("[AUTO-BID] Broadcasted {} for product {}", messageType, product.getProductId());
    }

    // Extra method to check and renew auction
    private void checkAndRenewAuction(Product product) {
        if (product.getIsAutoRenew()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = product.getEndTime();

            Duration triggerDuration = auctionProperties.getTriggerDuration();
            Duration extendDuration = auctionProperties.getExtendDuration();

            if (triggerDuration.compareTo(Duration.between(now, endTime)) >= 0) {
                product.setEndTime(endTime.plus(extendDuration));
                _productRepository.save(product);
            }

            bidMessagingTemplate.convertAndSend(
                    "/topic/product/" + product.getProductId() + "/auction-extended", product.getEndTime());

            log.info("[AUTO-BID] Auction extended for product {}", product.getProductId());
        }
    }

    @Override
    public List<Bid> getTop5BidsByProductId(Integer productId) {
        return _bidRepository.findTop5ByProductProductIdOrderByBidPriceDescBidAtAsc(productId);
    }

    @Override
    public Bid getBid(Integer bidId) {
        return _bidRepository.findById(bidId)
                .orElseThrow(() -> new IllegalArgumentException("Bid not found"));
    }
}
