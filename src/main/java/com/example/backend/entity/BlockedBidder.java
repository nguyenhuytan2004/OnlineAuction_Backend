package com.example.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "blocked_bidder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "product",
    "blocker",
    "blocked"
})
@Schema(description = "Entity representing a blocked bidder on a product")
public class BlockedBidder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "block_id")
  @Schema(description = "Unique identifier for the block")
  private Integer blockId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  @Schema(description = "The product where bidder is blocked")
  private Product product;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "blocker_id", nullable = false)
  @Schema(description = "The user who blocked the bidder")
  private User blocker;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "blocked_id", nullable = false)
  @Schema(description = "The user who is blocked")
  private User blocked;

  @Column(name = "reason")
  @Schema(description = "Reason for blocking")
  private String reason;

  @CreationTimestamp
  @Column(name = "blocked_at")
  @Schema(description = "Timestamp when the block was created")
  private LocalDateTime blockedAt;
}
