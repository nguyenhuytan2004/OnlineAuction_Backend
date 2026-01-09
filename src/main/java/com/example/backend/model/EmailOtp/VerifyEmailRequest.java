package com.example.backend.model.EmailOtp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for verifying email with OTP")
public class VerifyEmailRequest {
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  @Schema(description = "Email address to verify", example = "user@example.com", required = true, format = "email")
  private String email;

  @NotBlank(message = "OTP không được để trống")
  @Size(min = 6, max = 6, message = "OTP phải có đúng 6 ký tự")
  @Schema(description = "Six-digit OTP code sent to email", example = "123456", required = true, minLength = 6, maxLength = 6)
  private String otp;
}
