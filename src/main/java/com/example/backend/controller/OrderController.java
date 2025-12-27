package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.AuctionOrder.CancelOrderRequest;
import com.example.backend.model.AuctionOrder.OrderStatusResponse;
import com.example.backend.model.AuctionOrder.PayOrderRequest;
import com.example.backend.model.AuctionOrder.PayOrderResponse;
import com.example.backend.model.AuctionOrder.SetShippingAddressRequest;
import com.example.backend.service.implement.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/pay")
  public ResponseEntity<?> payAndCreate(@RequestBody @Valid PayOrderRequest req) {
    try {
      PayOrderResponse orderResponse = orderService.payAndCreateOrder(req);
      return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    } catch (RuntimeException e) {
      log.error("[CONTROLLER][POST][ORDER - PAY] /api/orders/pay - {}", e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ORDER - PAY] /api/orders/pay - {}", e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{orderId}/status")
  public ResponseEntity<?> getStatus(@PathVariable Integer orderId) {
    try {
      OrderStatusResponse orderStatusResponse = orderService.getStatus(orderId);

      return new ResponseEntity<>(orderStatusResponse, HttpStatus.OK);
    } catch (RuntimeException e) {
      log.error("[CONTROLLER][GET][ORDER - STATUS] /api/orders/{}/status - {}", orderId, e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ORDER - STATUS] /api/orders/{}/status - {}", orderId, e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/{orderId}/shipping-address")
  public ResponseEntity<?> setAddress(
      @PathVariable Integer orderId,
      @RequestBody @Valid SetShippingAddressRequest req) {
    try {
      orderService.setShippingAddress(orderId, req);

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      log.error("[CONTROLLER][PUT][ORDER - SET SHIPPING ADDRESS] /api/orders/{}/shipping-address - {}", orderId,
          e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][PUT][ORDER - SET SHIPPING ADDRESS] /api/orders/{}/shipping-address - {}", orderId,
          e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/{orderId}/confirm-payment")
  public ResponseEntity<?> sellerConfirmPayment(@PathVariable Integer orderId) {
    try {
      orderService.sellerConfirmPayment(orderId);

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      log.error("[CONTROLLER][POST][ORDER - SELLER CONFIRM PAYMENT] /api/orders/{}/confirm-payment - {}", orderId,
          e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ORDER - SELLER CONFIRM PAYMENT] /api/orders/{}/confirm-payment - {}", orderId,
          e.getMessage(), e);

      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/{orderId}/confirm-received")
  public ResponseEntity<?> buyerConfirmReceived(@PathVariable Integer orderId) {
    try {
      orderService.buyerConfirmReceived(orderId);

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      log.error("[CONTROLLER][POST][ORDER - BUYER CONFIRM RECEIVED] /api/orders/{}/confirm-received - {}", orderId,
          e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ORDER - BUYER CONFIRM RECEIVED] /api/orders/{}/confirm-received - {}", orderId,
          e.getMessage(), e);

      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/{orderId}/cancel")
  public ResponseEntity<?> cancel(
      @PathVariable Integer orderId,
      @RequestBody @Valid CancelOrderRequest req) {
    try {
      orderService.cancel(orderId, req);

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      log.error("[CONTROLLER][PATCH][ORDER - CANCEL] /api/orders/{}/cancel - {}", orderId, e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][PATCH][ORDER - CANCEL] /api/orders/{}/cancel - {}", orderId, e.getMessage(), e);

      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
