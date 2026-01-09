package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.ToString;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Auction result entity representing the outcome of a completed auction")
@Entity
@Table(name = "AUCTION_RESULT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "product",
    "winner"
})
public class AuctionResult {

  public enum PaymentStatus {
    PENDING,
    PAID,
    CANCELED
  }

  @Schema(description = "Unique auction result identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "result_id")
  private Integer resultId;

  @Schema(description = "The product that was auctioned")
  @JoinColumn(name = "product_id", nullable = false, unique = true)
  @OneToOne(fetch = FetchType.EAGER)
  private Product product;

  @Schema(description = "The user who won the auction")
  @JoinColumn(name = "winner_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private User winner;

  @Schema(description = "The final winning bid amount", example = "250.75", minimum = "0")
  @Column(name = "final_price", nullable = false)
  private BigDecimal finalPrice;

  @Schema(description = "Timestamp when the auction ended", format = "date-time")
  @CreationTimestamp
  @Column(name = "result_time")
  private LocalDateTime resultTime;

  @Schema(description = "Current payment status of the auction result", example = "PENDING", allowableValues = {
      "PENDING", "PAID", "CANCELED" })
  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus;
}
