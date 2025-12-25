package com.example.backend.model.Product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProductRequest {
  @NotNull(message = "Category ID cannot be null")
  private Integer categoryId;

  @NotBlank(message = "Main URL cannot be blank")
  private String mainImageUrl;

  @Size(min = 3, message = "Auxiliary image URLs cannot exceed 10")
  private List<String> auxiliaryImageUrls;

  @NotBlank(message = "Product name cannot be blank")
  private String productName;

  private BigDecimal buyNowPrice;

  @NotNull(message = "Start price cannot be null")
  private BigDecimal startPrice;

  @NotNull(message = "Price step cannot be null")
  private BigDecimal priceStep;

  private String description;

  private Boolean isAutoRenew = false;
}
