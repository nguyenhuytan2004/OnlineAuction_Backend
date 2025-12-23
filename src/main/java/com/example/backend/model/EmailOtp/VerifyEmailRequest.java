package com.example.backend.model.EmailOtp;

import lombok.Data;

@Data
public class VerifyEmailRequest {
  private String email;
  private String otp;
}
