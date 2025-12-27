package com.example.backend.model.AuctionOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelOrderRequest {

  @NotBlank(message = "Cancel reason cannot be blank")
  @Size(max = 500, message = "Cancel reason must not exceed 500 characters")
  private String reason;
}
