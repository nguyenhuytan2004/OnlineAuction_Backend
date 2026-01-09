package com.example.backend.model.User;

import com.example.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request model for admin to update user information")
public class UpdateUserAdminRequest {

  @NotBlank(message = "Full name cannot be blank")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  @Schema(description = "Full name of the user", example = "John Doe", required = true, maxLength = 100)
  private String fullName;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email format is invalid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Schema(description = "Email address", example = "john@example.com", required = true, format = "email")
  private String email;

  @Schema(description = "User role in system", example = "SELLER", allowableValues = { "BIDDER", "SELLER", "ADMIN" })
  private User.Role role;

  @Schema(description = "Whether user account is active", example = "true")
  private Boolean isActive;
}
