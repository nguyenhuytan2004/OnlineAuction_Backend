package com.example.backend.model.AuctionOrder;

import com.example.backend.entity.AuctionOrder.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderResponse {
    private Integer orderId;
    private OrderStatus status;
}
