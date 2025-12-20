package com.example.backend.service.implement;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.AuctionOrder.OrderStatus;
import com.example.backend.entity.Product;
import com.example.backend.model.AuctionOrder.OrderStatusResponse;
import com.example.backend.model.AuctionOrder.PayOrderRequest;
import com.example.backend.model.AuctionOrder.PayOrderResponse;
import com.example.backend.model.AuctionOrder.SetShippingAddressRequest;
import com.example.backend.repository.IAuctionOrderRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final IAuctionOrderRepository orderRepo;
    private final IProductRepository productRepo;

    @Override
    @Transactional
    public PayOrderResponse payAndCreateOrder(PayOrderRequest req) {

        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getHighestBidder() == null) {
            throw new RuntimeException("Auction has no winner");
        }

        BigDecimal finalPrice = product.getCurrentPrice();
        if (req.getAmount().compareTo(finalPrice) != 0) {
            throw new RuntimeException("Payment amount mismatch");
        }

        AuctionOrder existing = orderRepo.findByProductId(product.getProductId()).orElse(null);

        if (existing != null) {

            if (existing.getStatus() != OrderStatus.WAIT_PAYMENT) {
                return new PayOrderResponse(
                        existing.getOrderId(),
                        existing.getStatus()
                );
            }

            existing.setPaidAt(Instant.now());
            existing.setStatus(OrderStatus.PAID);

            return new PayOrderResponse(
                    existing.getOrderId(),
                    existing.getStatus()
            );
        }
        AuctionOrder order = new AuctionOrder();
        order.setProductId(product.getProductId());
        order.setSellerId(product.getSeller().getUserId());
        order.setBuyerId(product.getHighestBidder().getUserId());
        order.setFinalPrice(finalPrice);
        order.setPaidAt(Instant.now());
        order.setStatus(OrderStatus.PAID);

        orderRepo.save(order);

        return new PayOrderResponse(order.getOrderId(), order.getStatus());
    }

    @Override
    @Transactional
    public OrderStatusResponse getStatus(Integer orderId) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderStatusResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getShippingAddress() != null && !order.getShippingAddress().isBlank(),
                order.getFinalPrice()
        );
    }

    @Override
    @Transactional
    public void setShippingAddress(Integer orderId, SetShippingAddressRequest req) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID)
            throw new RuntimeException("Cannot set address in status " + order.getStatus());

        order.setShippingAddress(req.getShippingAddress());
    }

    @Override
    @Transactional
    public void sellerConfirmPayment(Integer orderId) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID)
            throw new RuntimeException("Invalid order status");

        if (order.getShippingAddress() == null || order.getShippingAddress().isBlank())
            throw new RuntimeException("Shipping address not provided");

        order.setStatus(OrderStatus.ON_DELIVERING);
    }
}
