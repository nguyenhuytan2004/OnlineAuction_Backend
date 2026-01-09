package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Bid entity representing a bidder's offer on a product")
@Entity
@Table(name = "BID")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
    "product",
    "bidder"
})
public class Bid {

  @Schema(description = "Unique bid identifier", example = "789", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bid_id")
  private Integer bidId;

  @Schema(description = "The product being bid on")
  @NotNull(message = "Product must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Schema(description = "The user placing the bid")
  @NotNull(message = "Bidder must not be null")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "bidder_id", nullable = false)
  private User bidder;

  @Schema(description = "The bid amount offered", example = "150.50", minimum = "0", required = true)
  @NotNull(message = "Bid price must not be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Bid price must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid bid price")
  @Column(name = "bid_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal bidPrice;

  @Schema(description = "Maximum automatic bid price the bidder is willing to pay", example = "200.00", minimum = "0", required = true)
  @NotNull(message = "Max auto price must not be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Max auto price must be greater than 0")
  @Digits(integer = 16, fraction = 2, message = "Invalid max auto price")
  @Column(name = "max_auto_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal maxAutoPrice;

  @Schema(description = "Timestamp when the bid was placed", format = "date-time")
  @CreationTimestamp
  @Column(name = "bid_at", nullable = false, updatable = false)
  private LocalDateTime bidAt;
}
