package com.example.backend.model.Bid;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBidRequest {

    @NotNull(message = "Product ID is not null")
    private Integer productId;

    @NotNull(message = "Bidder ID is not null")
        private Integer bidderId;

    @NotNull(message = "Bid price is not null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Max auto price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Invalid max auto price")
    private BigDecimal maxAutoPrice;
}
