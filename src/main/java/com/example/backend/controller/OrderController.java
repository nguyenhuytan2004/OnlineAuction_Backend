package com.example.backend.controller;

import com.example.backend.model.AuctionOrder.PayOrderRequest;
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
}
