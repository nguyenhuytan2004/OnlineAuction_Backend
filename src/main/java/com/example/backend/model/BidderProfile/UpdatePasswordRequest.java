package com.example.backend.model.BidderProfile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for updating user password")
public class UpdatePasswordRequest {
  @NotNull(message = "Current password is required")
  @Size(min = 6, message = "Current password must be at least 8 characters long")
  @Size(max = 50, message = "Current password must not exceed 50 characters")
  @Schema(description = "Current password for verification", example = "oldPassword123", required = true, minLength = 6, maxLength = 50)
  private String currentPassword;

  @NotNull(message = "New password is required")
  @Size(min = 6, message = "New password must be at least 8 characters long")
  @Size(max = 50, message = "New password must not exceed 50 characters")
  @Schema(description = "New password to set", example = "newPassword456", required = true, minLength = 6, maxLength = 50)
  private String newPassword;

  @NotNull(message = "Confirm new password is required")
  @Size(min = 6, message = "Confirm new password must be at least 8 characters long")
  @Size(max = 50, message = "Confirm new password must not exceed 50 characters")
  @Schema(description = "Confirmation of new password", example = "newPassword456", required = true, minLength = 6, maxLength = 50)
  private String confirmPassword;
}
