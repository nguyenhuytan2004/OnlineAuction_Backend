package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_transaction")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

  public enum TransactionType {
    UPGRADE_SELLER,
    PURCHASE_PRODUCT
  }

  public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
  }

  @Id
  @Schema(description = "Unique order code for the transaction", example = "1234567890", required = true)
  @Column(name = "order_code", nullable = false, unique = true)
  private Long orderCode;

  @Schema(description = "ID of the user who initiated the transaction", example = "1", required = true)
  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Schema(description = "Amount of the transaction in cents", example = "10000", required = true)
  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Schema(description = "Type of the transaction", example = "UPGRADE_SELLER", required = true)
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TransactionType type; // 'UPGRADE_SELLER' hoặc 'PURCHASE_PRODUCT'

  @Schema(description = "Status of the transaction", example = "PENDING", required = true)
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransactionStatus status; // 'PENDING', 'COMPLETED', 'FAILED'

  @Schema(description = "ID of the product associated with the transaction", example = "1", required = false)
  @Column(name = "product_id")
  private Integer productId; // Có thể null nếu type là 'UPGRADE_SELLER'
}
