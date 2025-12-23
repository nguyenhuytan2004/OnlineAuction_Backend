package com.example.backend.model.Momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomoIpnRequest {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private String orderInfo;
    private Integer resultCode;
    private String message;
    private String transId;
    private Long responseTime;
    private String extraData;

    private String signature;
}