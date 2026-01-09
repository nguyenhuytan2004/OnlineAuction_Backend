package com.example.backend.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request model for updating an existing auction product")
public class UpdateProductRequest {
  @NotNull(message = "Category ID cannot be null")
  @Schema(description = "ID of the product category", example = "1", required = true)
  private Integer categoryId;

  @NotBlank(message = "Main image URL cannot be blank")
  @Size(max = 255, message = "Main image URL cannot exceed 255 characters")
  @Schema(description = "Main product image URL", example = "https://example.com/image.jpg", required = true)
  private String mainImageUrl;

  @NotBlank(message = "Product name cannot be blank")
  @Size(max = 255, message = "Product name cannot exceed 255 characters")
  @Schema(description = "Product name", example = "iPhone 14 Pro", required = true)
  private String productName;

  @Min(value = 0, message = "Current price must be greater than or equal to 0")
  @Schema(description = "Current auction price", example = "450.00", minimum = "0")
  private BigDecimal currentPrice;

  @Min(value = 0, message = "Buy now price must be greater than or equal to 0")
  @Schema(description = "Buy now price if available", example = "600.00", minimum = "0")
  private BigDecimal buyNowPrice;

  @NotNull(message = "Start price cannot be null")
  @Min(value = 0, message = "Start price must be greater than or equal to 0")
  @Schema(description = "Initial starting price", example = "100.00", minimum = "0", required = true)
  private BigDecimal startPrice;

  @NotNull(message = "Price step cannot be null")
  @Min(value = 0, message = "Price step must be greater than or equal to 0")
  @Schema(description = "Minimum price increment for bids", example = "10.00", minimum = "0", required = true)
  private BigDecimal priceStep;

  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  @Schema(description = "Detailed product description", example = "Brand new, never used device")
  private String description;

  @NotNull(message = "End time cannot be null")
  @Schema(description = "Auction end date and time", example = "2024-01-15T18:00:00", required = true, format = "date-time")
  private LocalDateTime endTime;

  @NotNull(message = "isAutoRenew cannot be null")
  @Schema(description = "Whether auction auto-renews if no winner", example = "true", required = true)
  private Boolean isAutoRenew;

  @NotNull(message = "allowUnratedBidder cannot be null")
  @Schema(description = "Allow bidders without ratings to participate", example = "true", required = true)
  private Boolean allowUnratedBidder;

  @NotNull(message = "isActive cannot be null")
  @Schema(description = "Product availability status", example = "true", required = true)
  private Boolean isActive;
}
