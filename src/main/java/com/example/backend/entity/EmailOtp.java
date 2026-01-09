package com.example.backend.entity;

import java.time.LocalDateTime;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Email OTP entity for managing one-time passwords for email verification and password reset")
@Entity
@Table(name = "email_otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailOtp {

  public enum OtpType {
    VERIFY_EMAIL,
    RESET_PASSWORD
  }

  @Schema(description = "Unique OTP record identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Schema(description = "Email address associated with this OTP", example = "user@example.com", required = true)
  @Column(nullable = false)
  private String email;

  @Schema(description = "The one-time password code", example = "123456", required = true)
  @Column(nullable = false)
  private String otp;

  @Schema(description = "Expiration timestamp of this OTP", format = "date-time", required = true)
  @Column(nullable = false)
  private LocalDateTime expiredAt;

  @Schema(description = "Timestamp when this OTP was created", format = "date-time")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Schema(description = "Type of OTP (email verification or password reset)", example = "VERIFY_EMAIL", allowableValues = {
      "VERIFY_EMAIL", "RESET_PASSWORD" }, required = true)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OtpType type;
}