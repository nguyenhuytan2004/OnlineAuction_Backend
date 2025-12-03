package com.example.backend.model.WebSocket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidUpdateMessage {
    public enum MessageType {
        NEWBID, OUTBID, LEADING
    }

    // Product info
    private Integer productId;
    private String productName;

    // Price info
    private BigDecimal currentPrice;
    private BigDecimal previousPrice;
    private BigDecimal priceStep;

    // Highest bidder info
    private Integer highestBidderId;
    private String highestBidderName;

    // New bid info
    private Integer newBidId;
    private Integer newBidderId;
    private String newBidderName;
    private BigDecimal newBidPrice;
    private BigDecimal newBidMaxPrice;
    private LocalDateTime bidAt;

    // Metadata
    private Integer totalBids;
    private MessageType messageType;
    private String message;
}