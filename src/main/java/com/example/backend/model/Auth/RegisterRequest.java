package com.example.backend.model.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "User registration request with account information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  @Schema(description = "User full name", example = "John Doe", required = true)
  private String fullName;

  @Schema(description = "User email address", example = "john@example.com", required = true)
  private String email;

  @Schema(description = "Account password", example = "password123", required = true)
  private String password;
}
