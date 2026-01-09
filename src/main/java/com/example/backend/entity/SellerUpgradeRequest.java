package com.example.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Seller upgrade request entity for bidders requesting seller account status")
@Entity
@Table(name = "seller_upgrade_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "user"
})
public class SellerUpgradeRequest {

  public enum Status {
    PENDING,
    APPROVED,
    REJECTED
  }

  @Schema(description = "Unique request identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "request_id")
  private Integer requestId;

  @Schema(description = "The user requesting seller status")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Schema(description = "Timestamp when the request was created", format = "date-time")
  @CreationTimestamp
  @Column(name = "request_at", updatable = false)
  private LocalDateTime requestAt;

  @Schema(description = "Current status of the request", example = "PENDING", allowableValues = { "PENDING", "APPROVED",
      "REJECTED" })
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status = Status.PENDING;

  @Schema(description = "Timestamp when the request was reviewed", format = "date-time")
  @Column(name = "reviewed_at")
  private LocalDateTime reviewedAt;

  @Schema(description = "Admin comments on the request decision", example = "Approved. User meets all seller requirements.")
  @Column(name = "comments", columnDefinition = "TEXT")
  private String comments;
}
