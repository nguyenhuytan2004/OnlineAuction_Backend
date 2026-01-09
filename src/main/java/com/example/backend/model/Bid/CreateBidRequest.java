package com.example.backend.model.Bid;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request model for placing a bid on a product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBidRequest {

  @Schema(description = "The ID of the product to bid on", example = "123", required = true)
  @NotNull(message = "Product ID is not null")
  private Integer productId;

  @Schema(description = "The ID of the bidder", example = "456", required = true)
  @NotNull(message = "Bidder ID is not null")
  private Integer bidderId;

  @Schema(description = "Maximum automatic bid price willing to be paid", example = "500.00", required = true, minimum = "0")
  @NotNull(message = "Bid price is not null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Max auto price must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid max auto price")
  private BigDecimal maxAutoPrice;
}
