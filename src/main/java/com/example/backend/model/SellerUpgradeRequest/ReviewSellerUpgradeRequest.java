package com.example.backend.model.SellerUpgradeRequest;

import com.example.backend.entity.SellerUpgradeRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewSellerUpgradeRequest {

    @NotNull(message = "Status must be provided (APPROVED or REJECTED)")
    private SellerUpgradeRequest.Status status;

    @Size(max = 500, message = "Comments must not exceed 500 characters")
    private String comments;
}
