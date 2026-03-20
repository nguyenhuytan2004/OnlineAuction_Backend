package com.example.backend.model.PayOS;

import com.example.backend.entity.PaymentTransaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request model for creating a PayOS payment link")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayOSPaymentLinkRequest {
  @Schema(description = "The name of the order")
  @NotBlank(message = "Order name cannot be blank")
  private String orderName;

  @Schema(description = "The description of the payment")
  @Size(max = 255, message = "Description cannot exceed 255 characters")
  private String description;
  @NotNull(message = "Amount cannot be null")
  @Schema(description = "The amount to be paid")
  private Integer amount;

  @Schema(description = "The ID of the user making the payment", example = "1")
  @NotNull(message = "User ID cannot be null")
  private Integer userId;

  @Schema(description = "The type of the transaction")
  @NotNull(message = "Transaction type cannot be null")
  private PaymentTransaction.TransactionType type;

  @Schema(description = "The ID of the product associated with the transaction", example = "1")
  private Integer productId; // Có thể null nếu type là 'UPGRADE_SELLER'

  @Schema(description = "The URL to redirect after payment")
  @NotBlank(message = "Redirect URL cannot be blank")
  private String returnUrl;

  @Schema(description = "The URL to redirect if payment is cancelled")
  @NotBlank(message = "Cancel URL cannot be blank")
  private String cancelUrl;
}