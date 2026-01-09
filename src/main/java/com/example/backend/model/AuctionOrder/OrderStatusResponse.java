package com.example.backend.model.AuctionOrder;

import java.math.BigDecimal;

import com.example.backend.entity.AuctionOrder.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response model containing order status and details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
  @Schema(description = "Unique order identifier", example = "789", required = true)
  private Integer orderId;

  @Schema(description = "Current order status", example = "PENDING", allowableValues = { "PENDING", "CONFIRMED",
      "SHIPPED", "DELIVERED", "CANCELED" })
  private OrderStatus status;

  @Schema(description = "Whether a shipping address has been set for the order", example = "true")
  private Boolean shippingAddressPresent;

  @Schema(description = "Final price of the order", example = "250.75", minimum = "0")
  private BigDecimal finalPrice;
}
