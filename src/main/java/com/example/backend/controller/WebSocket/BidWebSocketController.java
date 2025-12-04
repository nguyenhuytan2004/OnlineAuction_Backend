package com.example.backend.controller.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.backend.entity.Bid;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.service.IBidService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BidWebSocketController {

    @Autowired
    private IBidService _bidService;

    @Autowired
    private SimpMessagingTemplate bidMessageTemplate;

    @MessageMapping("/product/{productId}/place-bid")
    public void placeBid(
            @DestinationVariable Integer productId,
            @Valid @Payload CreateBidRequest request) {
        try {
            log.info("[WS] Received bid for product {} from bidder {}",
                    productId, request.getBidderId());

            if (!request.getProductId().equals(productId)) {
                throw new IllegalArgumentException("Product ID mismatch");
            }

            // Place bid - Service sẽ tự broadcast
            Bid savedBid = _bidService.placeBid(request);

            // Gửi confirmation về cho bidder
            bidMessageTemplate.convertAndSendToUser(
                    request.getBidderId().toString(),
                    "/queue/bid-confirmation", savedBid
            );

        } catch (IllegalArgumentException e) {
            log.warn("[WS] Invalid bid: {}", e.getMessage());

            bidMessageTemplate.convertAndSendToUser(
                    request.getBidderId().toString(),
                    "/queue/bid-error", "Invalid bid: " + e.getMessage()
            );

        } catch (Exception e) {
            log.error("[WS] Error placing bid: {}", e.getMessage(), e);

            bidMessageTemplate.convertAndSendToUser(
                    request.getBidderId().toString(),
                    "/queue/bid-error", "Error placing bid: " + e.getMessage()
            );
        }
    }

}
