package com.example.backend.model.AuctionOrder;

import com.example.backend.entity.AuctionOrder.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model for order payment completion")
public class PayOrderResponse {
  @Schema(description = "Order ID", example = "456", required = true)
  private Integer orderId;

  @Schema(description = "Current order status after payment", example = "CONFIRMED", required = true, allowableValues = {
      "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELED" })
  private OrderStatus status;
}
