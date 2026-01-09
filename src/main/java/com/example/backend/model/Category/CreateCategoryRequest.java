package com.example.backend.model.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Request model for creating a new product category")
@Data
public class CreateCategoryRequest {
  @Schema(description = "Category name/title", example = "Electronics", required = true)
  @NotBlank(message = "Category name must not be blank")
  private String categoryName;

  @Schema(description = "Detailed description of the category", example = "All electronic devices and accessories")
  @Size(max = 500, message = "Description must not exceed 500 characters")
  private String description;

  @Schema(description = "Parent category ID if this is a subcategory", example = "1")
  private Integer parentCategoryId;
}
