package com.example.backend.model.AuctionOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request model for cancelling an auction order")
public class CancelOrderRequest {
  @Schema(description = "Reason for cancelling the auction order", example = "Found a better price elsewhere", required = true)
  @NotBlank(message = "Cancel reason cannot be blank")
  @Size(max = 500, message = "Cancel reason must not exceed 500 characters")
  private String reason;
}
