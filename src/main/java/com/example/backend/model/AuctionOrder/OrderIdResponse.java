package com.example.backend.model.AuctionOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model containing an order ID")
public class OrderIdResponse {
  @Schema(description = "The ID of the auction order", example = "789", required = true)
  private Integer orderId;
}
