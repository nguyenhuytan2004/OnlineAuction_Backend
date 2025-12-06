package com.example.backend.service;

import java.math.BigDecimal;

import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.model.WebSocket.BidUpdateMessage.MessageType;

public interface IAuctionService {
    public void broadcastAuctionUpdate(Product product,
            Bid newBid, BigDecimal previousPrice,
            MessageType messageType, String message);

    public void checkAndRenewAuction(Product product);

    public void updateAuctionResult(Product product);

    public void broadcastAuctionEnd(Product product, String reason);
}
