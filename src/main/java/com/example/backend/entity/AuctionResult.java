package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

@Entity
@Table(name = "AUCTION_RESULT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResult {

  public enum PaymentStatus {
    PENDING,
    PAID,
    CANCELED

  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "result_id")
  private Integer resultId;

  @JoinColumn(name = "product_id", nullable = false, unique = true)
  @OneToOne(fetch = FetchType.EAGER)
  private Product product;

  @JoinColumn(name = "winner_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private User winner;

  @Column(name = "final_price", nullable = false)
  private BigDecimal finalPrice;

  @CreationTimestamp
  @Column(name = "result_time")
  private LocalDateTime resultTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus;
}
