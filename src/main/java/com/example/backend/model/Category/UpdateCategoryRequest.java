package com.example.backend.model.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request model for updating a product category")
public class UpdateCategoryRequest {
  @NotBlank(message = "Category name must not be blank")
  @Schema(description = "Category name", example = "Electronics", required = true)
  private String categoryName;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  @Schema(description = "Category description", example = "Electronic devices and accessories", maxLength = 500)
  private String description;

  @Schema(description = "Parent category ID for hierarchy support", example = "5")
  private Integer parentCategoryId;
}
