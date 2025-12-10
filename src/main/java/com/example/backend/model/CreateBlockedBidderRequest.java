package com.example.backend.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBlockedBidderRequest {
    @NotNull(message = "Blocked ID cannot be null")
    private Integer blockedId;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;
}
