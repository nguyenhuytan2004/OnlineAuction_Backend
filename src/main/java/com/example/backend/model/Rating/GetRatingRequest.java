package com.example.backend.model.Rating;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetRatingRequest {
    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Reviewer ID is required")
    private Integer reviewerId;

    @NotNull(message = "Reviewee ID is required")
    private Integer revieweeId;
}
