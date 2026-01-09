package com.example.backend.model.Auth;

import com.example.backend.model.User.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication response containing access token and user information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  @Schema(description = "JWT access token for subsequent API requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "Refresh token for obtaining new access tokens", example = "refresh_token_value...")
  private String refreshToken;

  @Schema(description = "Authenticated user information")
  private UserResponse user;
}
