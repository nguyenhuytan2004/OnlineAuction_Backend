package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Auction order entity representing the order generated from a winning auction bid")
@Entity
@Table(name = "auction_order", uniqueConstraints = {
    @UniqueConstraint(columnNames = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

  @Schema(description = "Unique order identifier", example = "101", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Integer orderId;

  @Schema(description = "The product that was won in the auction")
  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Schema(description = "The seller of the product")
  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @Schema(description = "The buyer who won the auction")
  @ManyToOne
  @JoinColumn(name = "buyer_id", nullable = false)
  private User buyer;

  @Schema(description = "Final price the buyer agreed to pay", example = "250.75", minimum = "0")
  @Column(name = "final_price", nullable = false, precision = 15, scale = 2)
  private BigDecimal finalPrice;

  @Schema(description = "Current status of the order", example = "WAIT_PAYMENT", allowableValues = { "WAIT_PAYMENT",
      "PAID", "ON_DELIVERING", "COMPLETED", "CANCELED" })
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private OrderStatus status = OrderStatus.WAIT_PAYMENT;

  @Schema(description = "Timestamp when payment was received", format = "date-time")
  @Column(name = "paid_at")
  private Instant paidAt;

  @Schema(description = "Shipping address provided by the buyer")
  @Column(name = "shipping_address", columnDefinition = "TEXT")
  private String shippingAddress;

  @Schema(description = "Reason for order cancellation if applicable")
  @Column(name = "canceled_reason", columnDefinition = "TEXT")
  private String canceledReason;

  @Schema(description = "Order creation timestamp", format = "date-time")
  @Column(name = "created_at", updatable = false)
  @Builder.Default
  private Instant createdAt = Instant.now();
}
