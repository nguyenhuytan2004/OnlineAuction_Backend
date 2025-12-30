package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "auction_order", uniqueConstraints = {
    @UniqueConstraint(columnNames = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "product",
        "seller",
        "buyer"
})
public class AuctionOrder {

  public enum OrderStatus {
    WAIT_PAYMENT,
    PAID,
    ON_DELIVERING,
    COMPLETED,
    CANCELED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Integer orderId;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @ManyToOne
  @JoinColumn(name = "buyer_id", nullable = false)
  private User buyer;

  @Column(name = "final_price", nullable = false, precision = 15, scale = 2)
  private BigDecimal finalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.WAIT_PAYMENT;

  @Column(name = "paid_at")
  private Instant paidAt;

  @Column(name = "shipping_address", columnDefinition = "TEXT")
  private String shippingAddress;

  @Column(name = "canceled_reason", columnDefinition = "TEXT")
  private String canceledReason;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt = Instant.now();
}
