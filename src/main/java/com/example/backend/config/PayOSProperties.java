package com.example.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "payos")
public class PayOSProperties {
  private String clientId;
  private String apiKey;
  private String checksumKey;
  private String returnUrl;
  private String cancelUrl;
}
