package com.example.backend.controller;

import com.example.backend.config.MomoProperties;
import com.example.backend.model.Momo.CreateMomoPaymentRequest;
import com.example.backend.model.Momo.MomoIpnRequest;
import com.example.backend.service.implement.MomoPaymentService;
import com.example.backend.utils.MomoSignatureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@RestController
@RequestMapping("/api/payments/momo")
@RequiredArgsConstructor
public class MomoPaymentController {

    private final MomoPaymentService momoPaymentService;
    private final MomoProperties momo;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreateMomoPaymentRequest req) {

        String payUrl = momoPaymentService.createPayUrl(
                req.getAmount(),
                req.getOrderInfo()
        );

        return ResponseEntity.ok(
                Map.of("payUrl", payUrl)
        );
    }

    @PostMapping("/ipn")
    public ResponseEntity<?> ipn(@RequestBody MomoIpnRequest ipn) {

        String rawSignature =
                "amount=" + ipn.getAmount() +
                        "&extraData=" + (ipn.getExtraData() == null ? "" : ipn.getExtraData()) +
                        "&message=" + ipn.getMessage() +
                        "&orderId=" + ipn.getOrderId() +
                        "&orderInfo=" + ipn.getOrderInfo() +
                        "&orderType=" + "" +         // nếu IPN có field này thì điền đúng
                        "&partnerCode=" + ipn.getPartnerCode() +
                        "&payType=" + "" +           // nếu có
                        "&requestId=" + ipn.getRequestId() +
                        "&responseTime=" + ipn.getResponseTime() +
                        "&resultCode=" + ipn.getResultCode() +
                        "&transId=" + ipn.getTransId();

        boolean ok = MomoSignatureUtil.verify(momo.getSecretKey(), rawSignature, ipn.getSignature());
        if (!ok) return ResponseEntity.badRequest().body("Invalid signature");

        // TODO: cập nhật DB theo resultCode (0/9000 tuỳ flow), idempotency theo orderId/requestId
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/return")
    public ResponseEntity<?> returnUrl(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(params);
    }
}
