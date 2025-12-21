package com.example.backend.model.Category;

import com.example.backend.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailResponse {
  private Integer categoryId;
  private String categoryName;

  private ParentCategory parent;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ParentCategory {
    private Integer categoryId;
    private String categoryName;
  }

  public static CategoryDetailResponse from(Category category) {

    ParentCategory p = null;

    if (category.getParent() != null) {
      p = new ParentCategory(
          category.getParent().getCategoryId(),
          category.getParent().getCategoryName());
    }

    return new CategoryDetailResponse(
        category.getCategoryId(),
        category.getCategoryName(),
        p);
  }
}
