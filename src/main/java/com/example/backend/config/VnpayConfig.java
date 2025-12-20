package com.example.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
public class VnpayConfig {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.ipn-url}")
    private String ipnUrl;

    public String getRandomNumber(int len) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, len);
    }
}
