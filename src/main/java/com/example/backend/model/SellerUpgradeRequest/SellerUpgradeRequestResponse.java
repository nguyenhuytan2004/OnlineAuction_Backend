package com.example.backend.model.SellerUpgradeRequest;

import com.example.backend.entity.SellerUpgradeRequest;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@Schema(description = "Response model for seller upgrade request details")
public class SellerUpgradeRequestResponse {

  @Schema(description = "Seller upgrade request ID", example = "1", required = true)
  private Integer requestId;

  @Schema(description = "User ID requesting seller upgrade", example = "123", required = true)
  private Integer userId;

  @Schema(description = "Email of the requesting user", example = "user@example.com", required = true)
  private String userEmail;

  @Schema(description = "Full name of the requesting user", example = "John Doe", required = true)
  private String userFullName;

  @Schema(description = "Current status of the upgrade request", example = "PENDING", allowableValues = { "PENDING",
      "APPROVED", "REJECTED" }, required = true)
  private SellerUpgradeRequest.Status status;

  @Schema(description = "Timestamp when the request was created", format = "date-time", required = true)
  private LocalDateTime requestAt;

  @Schema(description = "Timestamp when the request was reviewed", format = "date-time")
  private LocalDateTime reviewedAt;

  @Schema(description = "Admin comments on the request", example = "Account approved for selling")
  private String comments;

  public SellerUpgradeRequestResponse(SellerUpgradeRequest request) {
    this.requestId = request.getRequestId();
    this.userId = request.getUser().getUserId();
    this.userEmail = request.getUser().getEmail();
    this.userFullName = request.getUser().getFullName();
    this.status = request.getStatus();
    this.requestAt = request.getRequestAt();
    this.reviewedAt = request.getReviewedAt();
    this.comments = request.getComments();
  }
}
