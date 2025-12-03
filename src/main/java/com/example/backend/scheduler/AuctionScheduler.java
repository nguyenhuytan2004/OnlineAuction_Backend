package com.example.backend.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuctionScheduler {

    @Autowired
    private IProductRepository _productRepository;

    @Autowired
    private IBidRepository _bidRepository;

    @Autowired
    private IAuctionResultRepository _auctionResultRepository;

    @Scheduled(fixedDelay = 60000) // Chạy mỗi 60 giây
    @Transactional
    public void processExpiredAuctions() {
        try {
            List<Product> expiredProducts = _productRepository.findExpiredProductsWithoutResult(LocalDateTime.now());

            if (expiredProducts.isEmpty()) {
                return;
            }

            for (Product product : expiredProducts) {
                try {
                    processProductAuction(product);
                } catch (Exception e) {
                    log.error("[SCHEDULER][AUCTION] Error processing product ID {}: {}", product.getProductId(),
                            e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("[SCHEDULER][AUCTION] Error in process expired auctions: {}", e.getMessage(), e);
        }
    }

    // Extra method to process each expired product auction
    private void processProductAuction(Product product) {
        AuctionResult existingResult = _auctionResultRepository
                .findByProductProductId(product.getProductId());
        if (existingResult != null) {
            return;
        }

        Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(product.getProductId());
        if (highestBid != null) {
            AuctionResult auctionResult = new AuctionResult();
            auctionResult.setProduct(product);
            auctionResult.setWinner(highestBid.getBidder());
            auctionResult.setFinalPrice(product.getCurrentPrice());
            auctionResult.setPaymentStatus(AuctionResult.PaymentStatus.PENDING);
            _auctionResultRepository.save(auctionResult);
        }

        product.setIsActive(false);
        _productRepository.save(product);
    }
}
