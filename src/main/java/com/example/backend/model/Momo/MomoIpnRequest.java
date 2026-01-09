package com.example.backend.model.Momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "IPN (Instant Payment Notification) request from MoMo gateway for payment confirmation")
public class MomoIpnRequest {
  @Schema(description = "Partner code from MoMo", example = "MOMOXX")
  private String partnerCode;

  @Schema(description = "Order ID linked to payment", example = "order456")
  private String orderId;

  @Schema(description = "Request ID from payment creation", example = "req123456")
  private String requestId;

  @Schema(description = "Payment amount", example = "500000")
  private Long amount;

  @Schema(description = "Order information", example = "Payment for auction product")
  private String orderInfo;

  @Schema(description = "Result code from MoMo (0 = success)", example = "0")
  private Integer resultCode;

  @Schema(description = "Response message from MoMo", example = "Successful")
  private String message;

  @Schema(description = "MoMo transaction ID", example = "2011123456789")
  private String transId;

  @Schema(description = "Response timestamp in milliseconds", example = "1640000000000")
  private Long responseTime;

  @Schema(description = "Extra data passed through transaction", example = "extra_info")
  private String extraData;

  @Schema(description = "Signature for IPN verification", example = "signature_hash")
  private String signature;
}