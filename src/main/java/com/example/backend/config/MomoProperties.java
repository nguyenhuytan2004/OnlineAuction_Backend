package com.example.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "momo")
public class MomoProperties {
  private String endpoint;
  private String partnerCode;
  private String accessKey;
  private String secretKey;
  private String redirectUrl;
  private String ipnUrl;
  private String requestType;
}