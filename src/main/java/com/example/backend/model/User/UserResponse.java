package com.example.backend.model.User;

import java.time.LocalDateTime;

import com.example.backend.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "User profile response containing user account information")
@Data
@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
  @Schema(description = "Unique user identifier", example = "123", required = true)
  private Integer userId;

  @Schema(description = "User full name", example = "John Doe", required = true)
  private String fullName;

  @Schema(description = "User email address", example = "john@example.com", required = true)
  private String email;

  @Schema(description = "User rating score (sum of all ratings)", example = "450", minimum = "0")
  private Integer ratingScore;

  @Schema(description = "Total number of ratings received by the user", example = "50", minimum = "0")
  private Integer ratingCount;

  @Schema(description = "User role (BIDDER, SELLER, ADMIN)", example = "BIDDER", allowableValues = { "BIDDER", "SELLER",
      "ADMIN" })
  private User.Role role;

  @Schema(description = "Timestamp when seller status expires (null if still active seller)", format = "date-time")
  private LocalDateTime sellerExpiresAt;

  @Schema(description = "Whether the user account is active", example = "true")
  private Boolean isActive;

  @Schema(description = "Account creation timestamp", format = "date-time")
  private LocalDateTime createdAt;

  public UserResponse(User user) {
    this.userId = user.getUserId();
    this.fullName = user.getFullName();
    this.email = user.getEmail();
    this.ratingScore = user.getRatingScore();
    this.ratingCount = user.getRatingCount();
    this.role = user.getRole();
    this.sellerExpiresAt = user.getSellerExpiresAt() == null ? null : user.getSellerExpiresAt();
    this.isActive = user.getIsActive();
    this.createdAt = user.getCreatedAt();
  }
}
