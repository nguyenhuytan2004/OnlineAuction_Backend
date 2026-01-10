package com.example.backend.model.Captcha;

import lombok.Data;

@Data
public class GoogleRecaptchaResponse {
  private boolean success;
  private String challenge_ts;
  private String hostname;
}
