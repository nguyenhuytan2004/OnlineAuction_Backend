package com.example.backend.model.EmailOtp;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for verifying OTP during password reset")
public class VerifyResetPasswordOtpRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email format is invalid")
  @Schema(description = "Email address for password reset", example = "user@example.com", required = true, format = "email")
  private String email;

  @NotBlank(message = "OTP is required")
  @Size(min = 6, max = 6, message = "OTP must be exactly 6 characters")
  @Schema(description = "Six-digit OTP code for verification", example = "123456", required = true, minLength = 6, maxLength = 6)
  private String otp;
}
