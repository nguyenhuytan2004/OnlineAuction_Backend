package com.example.backend.model.SellerUpgradeRequest;

import com.example.backend.entity.SellerUpgradeRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for reviewing and deciding on a seller upgrade request")
public class ReviewSellerUpgradeRequest {

  @NotNull(message = "Status must be provided (APPROVED or REJECTED)")
  @Schema(description = "Admin decision on the upgrade request", example = "APPROVED", required = true, allowableValues = {
      "APPROVED", "REJECTED" })
  private SellerUpgradeRequest.Status status;

  @Size(max = 500, message = "Comments must not exceed 500 characters")
  @Schema(description = "Admin comments about the decision", example = "Account meets all seller requirements", maxLength = 500)
  private String comments;
}
