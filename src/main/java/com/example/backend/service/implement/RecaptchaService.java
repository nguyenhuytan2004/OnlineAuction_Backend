package com.example.backend.service.implement;

import com.example.backend.config.RecaptchaConfig;
import com.example.backend.model.Captcha.GoogleRecaptchaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RecaptchaService {

  private final RecaptchaConfig config;

  private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  public boolean verify(String token) {
    RestTemplate restTemplate = new RestTemplate();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", config.getSecretKey());
    params.add("response", token);

    GoogleRecaptchaResponse result =
            restTemplate.postForObject(VERIFY_URL, params, GoogleRecaptchaResponse.class);

    return result != null && result.isSuccess();
  }
}
