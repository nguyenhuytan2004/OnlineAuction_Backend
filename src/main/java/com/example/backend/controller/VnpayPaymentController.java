package com.example.backend.controller;

import com.example.backend.model.Vnpay.CreateVnpayPaymentRequest;
import com.example.backend.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/vnpay")
@RequiredArgsConstructor
public class VnpayPaymentController {

/*    private final IPaymentService vnPayPaymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @Valid @RequestBody CreateVnpayPaymentRequest req,
            HttpServletRequest request) {

        String ipAddr = request.getRemoteAddr();

        return ResponseEntity.ok(
                vnPayPaymentService.createPaymentUrl(
                        req.getOrderId(),
                        req.getAmount(),
                        ipAddr
                )
        );
    }

    @PostMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleIpn(
            @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(
                vnPayPaymentService.handleIpn(params)
        );
    }*/
}
