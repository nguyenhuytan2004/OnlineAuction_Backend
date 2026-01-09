package com.example.backend.model.Product;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Request model for creating a new auction product")
@Data
public class CreateProductRequest {

  @Schema(description = "Category ID for the product", example = "1", required = true)
  @NotNull(message = "Category ID cannot be null")
  private Integer categoryId;

  @Schema(description = "Main product image URL", example = "https://example.com/image.jpg", required = true)
  @NotBlank(message = "Main URL cannot be blank")
  private String mainImageUrl;

  @Schema(description = "List of auxiliary product image URLs (max 10)", example = "[\"https://example.com/img1.jpg\"]")
  @Size(min = 3, message = "Auxiliary image URLs cannot exceed 10")
  private List<String> auxiliaryImageUrls;

  @Schema(description = "Product name", example = "iPhone 14 Pro", required = true)
  @NotBlank(message = "Product name cannot be blank")
  private String productName;

  @Schema(description = "Buy now price (optional instant purchase price)", example = "500.00")
  private BigDecimal buyNowPrice;

  @Schema(description = "Starting bid price", example = "100.00", required = true, minimum = "0")
  @NotNull(message = "Start price cannot be null")
  private BigDecimal startPrice;

  @Schema(description = "Minimum price increment for each bid", example = "10.00", required = true, minimum = "0")
  @NotNull(message = "Price step cannot be null")
  private BigDecimal priceStep;

  @Schema(description = "Detailed product description (HTML allowed)", example = "Brand new iPhone 14 Pro in perfect condition")
  private String description;

  @Schema(description = "Auto-renew auction when no bids are received", example = "false", defaultValue = "false")
  private Boolean isAutoRenew = false;
}
