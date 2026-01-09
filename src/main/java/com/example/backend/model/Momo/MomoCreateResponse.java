package com.example.backend.model.Momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model from MoMo payment gateway after payment creation")
public class MomoCreateResponse {
  @Schema(description = "Result code indicating success or failure", example = "0", required = true)
  private Integer resultCode;

  @Schema(description = "Response message", example = "Successful", required = true)
  private String message;

  @Schema(description = "Payment URL to redirect user for MoMo payment", example = "https://payment.momo.vn/...", required = true)
  private String payUrl;
}