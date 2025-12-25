package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Category;
import com.example.backend.model.Category.CreateCategoryRequest;
import com.example.backend.model.Category.UpdateCategoryRequest;

public interface ICategoryService {
  public List<Category> getAllCategories();

  public Category createCategory(CreateCategoryRequest createCategoryRequest);

  public Category updateCategory(Integer id, UpdateCategoryRequest updateCategoryRequest);

  public Category getCategoryById(Integer id);

  void deleteCategory(Integer id);
}
