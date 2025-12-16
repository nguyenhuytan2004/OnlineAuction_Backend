package com.example.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@Entity
@Table(name = "\"USER\"")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  public enum Role {
    BIDDER, SELLER, ADMIN
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @NotBlank(message = "Full name must not be blank")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @NotBlank(message = "Email must not be blank")
  @Email(message = "Invalid email address")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @NotBlank(message = "Password must not be blank")
  @Size(max = 255, message = "Encrypted password must not exceed 255 characters")
  @Column(name = "encrypted_password", nullable = false, length = 255)
  @JsonIgnore
  private String encryptedPassword;

  @Min(value = 0, message = "Rating score must be greater than or equal to 0")
  @Column(name = "rating_score", columnDefinition = "INT DEFAULT 0")
  private Integer ratingScore = 0;

  @Min(value = 0, message = "Rating count must be greater than or equal to 0")
  @Column(name = "rating_count", columnDefinition = "INT DEFAULT 0")
  private Integer ratingCount = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, columnDefinition = "ENUM('BIDDER', 'SELLER', 'ADMIN') DEFAULT 'BIDDER'")
  private Role role = Role.BIDDER;

  @Column(name = "seller_expires_at")
  private LocalDateTime sellerExpiresAt;

  @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
  private Boolean isActive = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
  // private List<Product> sellingProducts;
  // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  // @JsonManagedReference("user-watchList")
  // private List<WatchList> watchList;
}
