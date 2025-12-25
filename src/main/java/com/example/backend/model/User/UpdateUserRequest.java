package com.example.backend.model.User;

import java.time.LocalDateTime;

import com.example.backend.entity.User.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email format is invalid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @NotBlank(message = "Full name cannot be blank")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  private String fullName;

  @NotNull(message = "Rating score cannot be null")
  @Min(value = 0, message = "Rating score must be greater than or equal to 0")
  private Integer ratingScore = 0;

  @NotNull(message = "Rating count cannot be null")
  @Min(value = 0, message = "Rating count must be greater than or equal to 0")
  private Integer ratingCount = 0;

  @NotNull(message = "Role cannot be null")
  @Enumerated(EnumType.STRING)
  private Role role;

  private LocalDateTime sellerExpiresAt;

  @NotNull(message = "isActive cannot be null")
  private Boolean isActive;
}
