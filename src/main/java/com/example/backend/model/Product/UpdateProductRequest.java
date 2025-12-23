package com.example.backend.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProductRequest {
  @NotNull(message = "Category ID cannot be null")
  private Integer categoryId;

  @NotBlank(message = "Main image URL cannot be blank")
  @Size(max = 255, message = "Main image URL cannot exceed 255 characters")
  private String mainImageUrl;

  @NotBlank(message = "Product name cannot be blank")
  @Size(max = 255, message = "Product name cannot exceed 255 characters")
  private String productName;

  @Min(value = 0, message = "Current price must be greater than or equal to 0")
  private BigDecimal currentPrice;

  @Min(value = 0, message = "Buy now price must be greater than or equal to 0")
  private BigDecimal buyNowPrice;

  @NotNull(message = "Start price cannot be null")
  @Min(value = 0, message = "Start price must be greater than or equal to 0")
  private BigDecimal startPrice;

  @NotNull(message = "Price step cannot be null")
  @Min(value = 0, message = "Price step must be greater than or equal to 0")
  private BigDecimal priceStep;

  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  private String description;

  @NotNull(message = "End time cannot be null")
  private LocalDateTime endTime;

  @NotNull(message = "isAutoRenew cannot be null")
  private Boolean isAutoRenew;

  @NotNull(message = "allowUnratedBidder cannot be null")
  private Boolean allowUnratedBidder;

  @NotNull(message = "isActive cannot be null")
  private Boolean isActive;
}
