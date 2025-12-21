package com.example.backend.model.AuctionOrder;

import com.example.backend.entity.AuctionOrder.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
    private Integer orderId;
    private OrderStatus status;
    private Boolean shippingAddressPresent;
    private BigDecimal finalPrice;
}
