package com.example.backend.service;

import java.math.BigDecimal;
import java.util.Map;

public interface IPaymentService {
    Map<String, Object> createPaymentUrl(
            Integer orderId,
            BigDecimal amount,
            String clientIp
    );

    //Map<String, String> handleIpn(Map<String, String> params);
}
