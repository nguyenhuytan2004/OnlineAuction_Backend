package com.example.backend.model.Vnpay;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateVnpayPaymentRequest {

    @NotNull(message = "Order ID cannot be null")
    private Integer orderId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
}