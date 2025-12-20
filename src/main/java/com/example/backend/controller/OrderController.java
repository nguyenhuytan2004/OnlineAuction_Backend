package com.example.backend.controller;

import com.example.backend.model.AuctionOrder.OrderStatusResponse;
import com.example.backend.model.AuctionOrder.PayOrderRequest;
import com.example.backend.model.AuctionOrder.SetShippingAddressRequest;
import com.example.backend.service.implement.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/pay")
    public ResponseEntity<?> payAndCreate(@RequestBody @Valid PayOrderRequest req) {
        return ResponseEntity.ok(orderService.payAndCreateOrder(req));
    }

    @GetMapping("/{orderId}/status")
    public OrderStatusResponse getStatus(@PathVariable Integer orderId) {
        return orderService.getStatus(orderId);
    }

    @PutMapping("/{orderId}/shipping-address")
    public ResponseEntity<?> setAddress(
            @PathVariable Integer orderId,
            @RequestBody @Valid SetShippingAddressRequest req) {

        orderService.setShippingAddress(orderId, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<?> sellerConfirmPayment(@PathVariable Integer orderId) {
        orderService.sellerConfirmPayment(orderId);
        return ResponseEntity.ok().build();
    }
}
