package com.example.backend.model.Momo;

import lombok.Data;

@Data
public class CreateMomoPaymentRequest {
  private Long amount;
  private String orderInfo;
}
