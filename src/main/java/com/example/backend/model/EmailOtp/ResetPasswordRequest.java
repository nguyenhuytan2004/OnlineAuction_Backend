package com.example.backend.model.EmailOtp;

import lombok.Data;

@Data
public class ResetPasswordRequest {
  private String email;
  private String newPassword;
}
