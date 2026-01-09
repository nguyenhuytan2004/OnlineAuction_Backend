package com.example.backend.model.User;

import java.time.LocalDateTime;

import com.example.backend.entity.User.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for creating a new user account")
public class CreateUserRequest {

  @NotBlank(message = "Full name cannot be blank")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  @Schema(description = "Full name of the user", example = "John Doe", required = true, maxLength = 100)
  private String fullName;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email format is invalid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Schema(description = "Email address", example = "john@example.com", required = true, format = "email")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  @Schema(description = "Account password (min 6 characters)", example = "password123", required = true, minLength = 6, maxLength = 100)
  private String password;

  @NotNull(message = "Role cannot be null")
  @Enumerated(EnumType.STRING)
  @Schema(description = "User role in the system", example = "BIDDER", required = true, allowableValues = { "BIDDER",
      "SELLER", "ADMIN" })
  private Role role;

  @Schema(description = "Seller account expiration date (if applicable)", format = "date-time")
  private LocalDateTime sellerExpiresAt;
}
