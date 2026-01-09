package com.example.backend.model.AuctionOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request model for setting shipping address for an order")
public class SetShippingAddressRequest {
  @NotBlank(message = "Shipping address cannot be blank")
  @Size(max = 1000, message = "Shipping address must not exceed 1000 characters")
  @Schema(description = "Complete shipping address", example = "123 Main Street, City, State 12345", required = true, maxLength = 1000)
  private String shippingAddress;
}
