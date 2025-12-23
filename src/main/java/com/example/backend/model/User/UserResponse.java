package com.example.backend.model.User;

import java.time.LocalDateTime;

import com.example.backend.entity.User;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
  private Integer userId;
  private String fullName;
  private String email;
  private Integer ratingScore;
  private Integer ratingCount;
  private User.Role role;
  private LocalDateTime sellerExpiresAt;
  private Boolean isActive;
  private LocalDateTime createdAt;

  public UserResponse(User user) {
    this.userId = user.getUserId();
    this.fullName = user.getFullName();
    this.email = user.getEmail();
    this.ratingScore = user.getRatingScore();
    this.ratingCount = user.getRatingCount();
    this.role = user.getRole();
    this.sellerExpiresAt = user.getSellerExpiresAt() == null ? null : user.getSellerExpiresAt();
    this.isActive = user.getIsActive();
    this.createdAt = user.getCreatedAt();
  }
}
