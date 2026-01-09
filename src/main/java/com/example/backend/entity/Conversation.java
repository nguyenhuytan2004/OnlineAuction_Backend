package com.example.backend.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Schema(description = "Conversation entity representing a chat thread between a seller and buyer about a product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conversation")
@ToString(exclude = {
    "product",
    "seller",
    "buyer"
})
public class Conversation {
  @Schema(description = "Unique conversation identifier", example = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "conversation_id")
  private Integer conversationId;

  @Schema(description = "The product being discussed in this conversation")
  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Schema(description = "The seller in this conversation")
  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @Schema(description = "The buyer in this conversation")
  @ManyToOne
  @JoinColumn(name = "buyer_id", nullable = false)
  private User buyer;

  @Schema(description = "Whether the conversation is currently active", example = "true")
  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Schema(description = "Timestamp when the conversation was created", format = "date-time")
  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private String createdAt;
}
