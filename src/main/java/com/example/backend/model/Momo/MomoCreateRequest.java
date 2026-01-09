package com.example.backend.model.Momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for creating a MoMo payment transaction")
public class MomoCreateRequest {
  @Schema(description = "Partner code for MoMo gateway", example = "MOMOXX")
  private String partnerCode;

  @Schema(description = "Partner name", example = "Online Auction")
  private String partnerName;

  @Schema(description = "Store ID", example = "store123")
  private String storeId;

  @Schema(description = "Unique request identifier", example = "req123456")
  private String requestId;

  @Schema(description = "Order ID to link with MoMo transaction", example = "order456")
  private String orderId;

  @Schema(description = "Payment amount in VND", example = "500000")
  private Long amount;

  @Schema(description = "Order information", example = "Payment for auction product")
  private String orderInfo;

  @Schema(description = "URL to redirect after payment", example = "https://example.com/payment/success")
  private String redirectUrl;

  @Schema(description = "URL for IPN notifications", example = "https://example.com/payment/ipn")
  private String ipnUrl;

  @Schema(description = "Request type for MoMo", example = "captureWallet")
  private String requestType;

  @Schema(description = "Extra data to pass through transaction", example = "extra_info")
  private String extraData;

  @Schema(description = "Language code", example = "vi")
  private String lang;

  @Schema(description = "Auto capture flag", example = "true")
  private Boolean autoCapture;

  @Schema(description = "Signature for request verification", example = "signature_hash")
  private String signature;
}