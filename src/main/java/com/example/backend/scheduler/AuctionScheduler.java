package com.example.backend.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Product;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IAuctionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuctionScheduler {

    @Autowired
    private IProductRepository _productRepository;

    @Autowired
    private IAuctionService _auctionService;

    @Scheduled(fixedDelay = 10000) // Chạy mỗi 10 giây
    @Transactional
    public void processExpiredAuctions() {
        try {
            List<Product> expiredProducts = _productRepository.findExpiredProductsWithoutResult(LocalDateTime.now());

            if (expiredProducts.isEmpty()) {
                return;
            }

            for (Product product : expiredProducts) {
                try {
                    _auctionService.updateAuctionResult(product);
                    _auctionService.broadcastAuctionEnd(product, "Phiên đấu giá đã kết thúc.");
                } catch (Exception e) {
                    log.error("[SCHEDULER][AUCTION] Error processing product ID {}: {}", product.getProductId(),
                            e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("[SCHEDULER][AUCTION] Error in process expired auctions: {}", e.getMessage(), e);
        }
    }

}
