package com.example.backend.model.EmailOtp;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for resetting user password")
public class ResetPasswordRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email format is invalid")
  @Schema(description = "Email address for the account", example = "user@example.com", required = true, format = "email")
  private String email;

  @NotBlank(message = "New password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  @Schema(description = "New password for the account", example = "newPassword123", required = true, minLength = 6, maxLength = 100)
  private String newPassword;
}
