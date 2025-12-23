package com.example.backend.model.Momo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMomoPaymentRequest {
    private Long amount;
    private String orderInfo;
}
