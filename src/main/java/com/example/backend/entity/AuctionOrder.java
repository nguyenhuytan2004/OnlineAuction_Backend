package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "auction_order",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "product_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionOrder {

    public enum OrderStatus {
        WAIT_PAYMENT,
        PAID_NO_ADDRESS,
        ON_DELIVERING,
        COMPLETED,
        CANCELLED
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name = "final_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.WAIT_PAYMENT;

    /* Payment */
    @Column(name = "paid_at")
    private Instant paidAt;

    /* Shipping */
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    /* Cancel */
    @Column(name = "cancelled_reason", columnDefinition = "TEXT")
    private String cancelledReason;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
