package com.example.backend.model.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {
  @NotBlank(message = "Category name must not be blank")
  private String categoryName;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  private String description;

  private Integer parentCategoryId;
}
