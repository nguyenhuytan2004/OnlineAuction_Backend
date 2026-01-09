package com.example.backend.model.Momo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Request model for creating a MoMo payment")
@Data
public class CreateMomoPaymentRequest {
  @Schema(description = "Payment amount in VND", example = "100000", required = true, minimum = "0")
  private Long amount;

  @Schema(description = "Order information or transaction description", example = "Payment for product auction #123", required = true)
  private String orderInfo;
}
