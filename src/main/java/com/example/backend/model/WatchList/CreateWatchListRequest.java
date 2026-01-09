package com.example.backend.model.WatchList;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request model for adding a product to user's watchlist")
public class CreateWatchListRequest {

  @NotNull(message = "User ID is required")
  @Schema(description = "ID of the user adding to watchlist", example = "123", required = true)
  private Integer userId;

  @NotNull(message = "Product ID is required")
  @Schema(description = "ID of the product to watch", example = "456", required = true)
  private Integer productId;
}
