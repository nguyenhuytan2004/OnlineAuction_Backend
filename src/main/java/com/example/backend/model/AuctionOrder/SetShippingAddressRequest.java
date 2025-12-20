package com.example.backend.model.AuctionOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetShippingAddressRequest {

    @NotBlank(message = "Shipping address cannot be blank")
    @Size(max = 1000, message = "Shipping address must not exceed 1000 characters")
    private String shippingAddress;
}
