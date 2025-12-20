package com.example.backend.model.AuctionOrder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayOrderRequest {

    @NotNull(message = "Product ID cannot be null")
    private Integer productId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    private String paymentRef;
}
