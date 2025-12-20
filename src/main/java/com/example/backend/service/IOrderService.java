package com.example.backend.service;

import com.example.backend.model.AuctionOrder.*;

public interface IOrderService {
    PayOrderResponse payAndCreateOrder(PayOrderRequest req);
    OrderStatusResponse getStatus(Integer orderId);
    void setShippingAddress(Integer orderId, SetShippingAddressRequest req);
    //void sellerConfirmPayment(Integer orderId);
    //void buyerConfirmReceived(Integer orderId);
    //void cancel(Integer orderId, CancelOrderRequest req);
}
