package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.PayOS.CreatePayOSPaymentLinkRequest;
import com.example.backend.service.implement.PayOSPaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.WebhookData;

@Slf4j
@RestController
@RequestMapping("/api/payment/payos")
public class PayOSPaymentController {

  @Autowired
  private PayOSPaymentService payOSPaymentService;

  @PostMapping(path = "/create-payment-link")
  public ResponseEntity<?> createPayOSPaymentLink(
      @RequestBody CreatePayOSPaymentLinkRequest createPayOSPaymentLinkRequest) {
    try {
      CreatePaymentLinkResponse data = payOSPaymentService.createPayOSPaymentLink(createPayOSPaymentLinkRequest);

      return new ResponseEntity<>(data, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][POST][PAYOS] /api/payment/payos/create-payment-link - Failed to create PayOS payment link: {}",
          e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(path = "/webhook-handler")
  public ResponseEntity<?> handlePayOSWebhook(@RequestBody Object body)
      throws JsonProcessingException, IllegalArgumentException {
    try {
      WebhookData data = payOSPaymentService.handlePayOSWebhook(body);

      return new ResponseEntity<>(data, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][POST][PAYOS] /api/payment/payos/webhook-handler - Failed to handle PayOS webhook: {}",
          e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
