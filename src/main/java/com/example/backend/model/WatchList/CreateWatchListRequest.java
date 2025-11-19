package com.example.backend.model.WatchList;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Product ID is required")
    private Integer productId;
}
