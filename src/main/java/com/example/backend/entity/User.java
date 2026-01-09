package com.example.backend.entity;

import java.time.LocalDateTime;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "User account entity representing a bidder, seller, or administrator in the online auction system")
@Entity
@Table(name = "\"USER\"")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "encryptedPassword"
})
public class User {

  public enum Role {
    BIDDER, SELLER, ADMIN
  }

  @Schema(description = "Unique user identifier", example = "123", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @Schema(description = "Full name of the user", example = "John Doe", required = true)
  @NotBlank(message = "Full name must not be blank")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Schema(description = "Email address of the user", example = "john@example.com", required = true)
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Invalid email address")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @Schema(description = "Encrypted password (not exposed in API)", required = true)
  @NotBlank(message = "Password must not be blank")
  @Size(max = 255, message = "Encrypted password must not exceed 255 characters")
  @Column(name = "encrypted_password", nullable = false, length = 255)
  @JsonIgnore
  private String encryptedPassword;

  @Schema(description = "User rating score (aggregate rating)", example = "4", minimum = "0")
  @Min(value = 0, message = "Rating score must be greater than or equal to 0")
  @Column(name = "rating_score", columnDefinition = "INT DEFAULT 0")
  private Integer ratingScore = 0;

  @Schema(description = "Number of ratings received by the user", example = "10", minimum = "0")
  @Min(value = 0, message = "Rating count must be greater than or equal to 0")
  @Column(name = "rating_count", columnDefinition = "INT DEFAULT 0")
  private Integer ratingCount = 0;

  @Schema(description = "User role in the system", example = "BIDDER", allowableValues = { "BIDDER", "SELLER",
      "ADMIN" }, required = true)
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, columnDefinition = "ENUM('BIDDER', 'SELLER', 'ADMIN') DEFAULT 'BIDDER'")
  private Role role = Role.BIDDER;

  @Schema(description = "Seller account expiration date/time", format = "date-time")
  @Column(name = "seller_expires_at")
  private LocalDateTime sellerExpiresAt;

  @Schema(description = "Whether the user account is active", example = "true")
  @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
  private Boolean isActive = true;

  @Schema(description = "Account creation timestamp", format = "date-time")
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Schema(description = "Whether the user email has been verified", example = "false")
  @Column(name = "is_verified")
  private Boolean isVerified = false;

  // @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
  // private List<Product> sellingProducts;
  // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  // @JsonManagedReference("user-watchList")
  // private List<WatchList> watchList;
}
