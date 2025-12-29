package com.example.backend.service;

import com.example.backend.entity.EmailOtp;

public interface IEmailOtpService {
  EmailOtp sendOtp(String email, EmailOtp.OtpType type);

  EmailOtp validateOtp(String email, String otp, EmailOtp.OtpType type);

}
