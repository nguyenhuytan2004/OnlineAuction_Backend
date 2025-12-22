package com.example.backend.model.Momo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMomoPaymentRequest {
    private Long orderId;
    private BigDecimal amount;
}
