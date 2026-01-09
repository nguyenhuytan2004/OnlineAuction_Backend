package com.example.backend.model.EmailOtp;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for initiating password reset process")
public class ForgotPasswordRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email format is invalid")
  @Schema(description = "Email address for password reset", example = "user@example.com", required = true, format = "email")
  private String email;
}
