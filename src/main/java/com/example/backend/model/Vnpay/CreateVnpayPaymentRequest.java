package com.example.backend.model.Vnpay;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Schema(description = "Request model for creating a VNPay payment")
public class CreateVnpayPaymentRequest {

  @NotNull(message = "Order ID cannot be null")
  @Schema(description = "ID of the order to be paid", example = "789", required = true)
  private Integer orderId;

  @NotNull(message = "Amount cannot be null")
  @Positive(message = "Amount must be greater than 0")
  @Schema(description = "Payment amount in VND", example = "500000.00", required = true, minimum = "0.01")
  private BigDecimal amount;
}