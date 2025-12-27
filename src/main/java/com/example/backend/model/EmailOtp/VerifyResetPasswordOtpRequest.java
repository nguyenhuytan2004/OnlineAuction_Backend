package com.example.backend.model.EmailOtp;

import lombok.Data;

@Data
public class VerifyResetPasswordOtpRequest {
    private String email;
    private String otp;
}
