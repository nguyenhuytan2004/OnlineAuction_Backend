package com.example.backend.model.Momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomoCreateRequest {
    private String partnerCode;
    private String partnerName;
    private String storeId;

    private String requestId;
    private String orderId;
    private Long amount;
    private String orderInfo;

    private String redirectUrl;
    private String ipnUrl;

    private String requestType;
    private String extraData;

    private String lang;
    private Boolean autoCapture;

    private String signature;
}