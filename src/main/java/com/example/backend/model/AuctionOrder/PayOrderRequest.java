package com.example.backend.model.AuctionOrder;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Request model for payment and order creation")
@Data
public class PayOrderRequest {

  @Schema(description = "The ID of the product being purchased", example = "123", required = true)
  @NotNull(message = "Product ID cannot be null")
  private Integer productId;

  @Schema(description = "Payment amount", example = "250.75", required = true, minimum = "0")
  @NotNull(message = "Amount cannot be null")
  @Positive(message = "Amount must be greater than 0")
  private BigDecimal amount;

  @Schema(description = "Payment reference or transaction ID", example = "PAY123456")
  @Size(max = 100, message = "Payment reference must not exceed 100 characters")
  private String paymentRef;
}
