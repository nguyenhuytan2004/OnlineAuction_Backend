package com.example.backend.service;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.Product;
import com.example.backend.model.AuctionOrder.CancelOrderRequest;
import com.example.backend.model.AuctionOrder.OrderStatusResponse;
import com.example.backend.model.AuctionOrder.PayOrderRequest;
import com.example.backend.model.AuctionOrder.PayOrderResponse;
import com.example.backend.model.AuctionOrder.SetShippingAddressRequest;

public interface IOrderService {
  AuctionOrder getAuctionOrderByProductId(Integer productId);

  void createAuctionOrder(Product product);

  PayOrderResponse payAndCreateOrder(PayOrderRequest req);

  OrderStatusResponse getStatus(Integer orderId);

  void setShippingAddress(Integer orderId, SetShippingAddressRequest req);

  void sellerConfirmPayment(Integer orderId);

  void buyerConfirmReceived(Integer orderId);

  void cancel(Integer orderId, CancelOrderRequest req);
}
