package com.example.backend.model.SellerUpgradeRequest;

import com.example.backend.entity.SellerUpgradeRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewSellerUpgradeRequest {

    @NotNull
    private SellerUpgradeRequest.Status status;

    private String comments;
}
