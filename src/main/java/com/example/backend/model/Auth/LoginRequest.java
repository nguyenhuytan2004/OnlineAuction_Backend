package com.example.backend.model.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "User login request with email and password credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
  @Schema(description = "User email address", example = "user@example.com", required = true)
  private String email;

  @Schema(description = "User password", example = "password123", required = true)
  private String password;
}
