package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.Category;

public interface ICategoryService {
    public List<Category> getAllCategories();
    public Category createCategory(Category category);
    public Category updateCategory(Integer id, Category request);
    public Category getCategoryById(Integer id);
    void deleteCategory(Integer id);
}
