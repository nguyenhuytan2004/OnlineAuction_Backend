package com.example.backend.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request model for blocking a bidder from a product")
public class CreateBlockedBidderRequest {
  @NotNull(message = "Blocked ID cannot be null")
  @Schema(description = "ID of the user to block", example = "123", required = true)
  private Integer blockedId;

  @Size(max = 255, message = "Reason cannot exceed 255 characters")
  @Schema(description = "Reason for blocking the bidder", example = "Non-payment history", maxLength = 255)
  private String reason;
}
