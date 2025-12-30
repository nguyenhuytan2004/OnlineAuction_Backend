package com.example.backend.controller;

import com.example.backend.config.MomoProperties;
import com.example.backend.model.Momo.CreateMomoPaymentRequest;
import com.example.backend.model.Momo.MomoIpnRequest;
import com.example.backend.service.implement.MomoPaymentService;
import com.example.backend.utils.MomoSignatureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/momo")
@RequiredArgsConstructor
@Slf4j
public class MomoPaymentController {

  private final MomoPaymentService momoPaymentService;
  private final MomoProperties momo;

  @PostMapping("/create")
  public ResponseEntity<?> createPayment(
          @RequestBody CreateMomoPaymentRequest req) {

    try {
      String payUrl = momoPaymentService.createPayUrl(
              req.getAmount(),
              req.getOrderInfo()
      );

      log.info(
              "[CONTROLLER][POST][MOMO] /api/payments/momo/create - Create payment success (amount={}, orderInfo={})",
              req.getAmount(), req.getOrderInfo()
      );

      return ResponseEntity.ok(Map.of("payUrl", payUrl));

    } catch (IllegalArgumentException iae) {
      log.warn(
              "[CONTROLLER][POST][MOMO] /api/payments/momo/create - Invalid request (amount={}, orderInfo={}): {}",
              req.getAmount(), req.getOrderInfo(), iae.getMessage()
      );
      return ResponseEntity.badRequest().body(iae.getMessage());

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][POST][MOMO] /api/payments/momo/create - Error occurred (amount={}, orderInfo={}): {}",
              req.getAmount(), req.getOrderInfo(), e.getMessage(), e
      );
      return ResponseEntity.internalServerError()
              .body("Failed to create MoMo payment");
    }
  }

  @PostMapping("/ipn")
  public ResponseEntity<?> ipn(@RequestBody MomoIpnRequest ipn) {

    try {
      String rawSignature =
              "amount=" + ipn.getAmount() +
                      "&extraData=" + (ipn.getExtraData() == null ? "" : ipn.getExtraData()) +
                      "&message=" + ipn.getMessage() +
                      "&orderId=" + ipn.getOrderId() +
                      "&orderInfo=" + ipn.getOrderInfo() +
                      "&orderType=" + "" +
                      "&partnerCode=" + ipn.getPartnerCode() +
                      "&payType=" + "" +
                      "&requestId=" + ipn.getRequestId() +
                      "&responseTime=" + ipn.getResponseTime() +
                      "&resultCode=" + ipn.getResultCode() +
                      "&transId=" + ipn.getTransId();

      boolean ok = MomoSignatureUtil.verify(
              momo.getSecretKey(),
              rawSignature,
              ipn.getSignature()
      );

      if (!ok) {
        log.warn(
                "[CONTROLLER][POST][MOMO] /api/payments/momo/ipn - Invalid signature (orderId={}, requestId={}, transId={})",
                ipn.getOrderId(), ipn.getRequestId(), ipn.getTransId()
        );
        return ResponseEntity.badRequest().body("Invalid signature");
      }

      log.info(
              "[CONTROLLER][POST][MOMO] /api/payments/momo/ipn - IPN received (orderId={}, requestId={}, resultCode={})",
              ipn.getOrderId(), ipn.getRequestId(), ipn.getResultCode()
      );

      return ResponseEntity.ok("OK");

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][POST][MOMO] /api/payments/momo/ipn - Error occurred (orderId={}): {}",
              ipn.getOrderId(), e.getMessage(), e
      );
      return ResponseEntity.internalServerError()
              .body("IPN processing failed");
    }
  }

  @GetMapping("/return")
  public ResponseEntity<?> returnUrl(
          @RequestParam Map<String, String> params) {

    log.info(
            "[CONTROLLER][GET][MOMO] /api/payments/momo/return - Return URL called (params={})",
            params.keySet()
    );

    return ResponseEntity.ok(params);
  }
}
