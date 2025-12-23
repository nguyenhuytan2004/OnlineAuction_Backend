package com.example.backend.service.implement;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Category;
import com.example.backend.model.Category.CreateCategoryRequest;
import com.example.backend.model.Category.UpdateCategoryRequest;
import com.example.backend.repository.ICategoryRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.ICategoryService;

@Service
public class CategoryService implements ICategoryService {
  @Autowired
  private ICategoryRepository _categoryRepository;

  @Autowired
  private IProductRepository _productRepository;

  @Override
  public List<Category> getAllCategories() {
    return _categoryRepository.findAll();
  }

  @Override
  public Category createCategory(CreateCategoryRequest createCategoryRequest) {
    Category newCategory = new Category();
    newCategory.setCategoryName(createCategoryRequest.getCategoryName());
    newCategory.setDescription(createCategoryRequest.getDescription());
    if (createCategoryRequest.getParentCategoryId() != null) {
      Category parent = _categoryRepository.findById(createCategoryRequest.getParentCategoryId())
          .orElseThrow(() -> new RuntimeException("Parent category not found"));

      newCategory.setParent(parent);
    } else {
      newCategory.setParent(null);
    }

    return _categoryRepository.save(newCategory);
  }

  @Override
  public Category updateCategory(Integer id, UpdateCategoryRequest updateCategoryRequest) {

    Category existing = _categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

    existing.setCategoryName(updateCategoryRequest.getCategoryName());
    if (updateCategoryRequest.getParentCategoryId() != null) {
      if (Objects.equals(updateCategoryRequest.getParentCategoryId(), id)) {
        throw new RuntimeException("Category cannot be parent of itself");
      }
      Category parent = _categoryRepository.findById(updateCategoryRequest.getParentCategoryId())
          .orElseThrow(() -> new RuntimeException("Parent category not found"));
      existing.setParent(parent);
    } else {
      existing.setParent(null);
    }

    existing.setDescription(updateCategoryRequest.getDescription());

    return _categoryRepository.save(existing);
  }

  @Override
  public Category getCategoryById(Integer id) {
    return _categoryRepository.findById(id).orElse(null);
  }

  @Override
  public void deleteCategory(Integer id) {

    Category category = _categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

    // 1. Check đang chứa sản phẩm nào không
    boolean hasProducts = _productRepository.existsByCategory_CategoryId(id);
    if (hasProducts) {
      throw new IllegalStateException("CATEGORY_HAS_PRODUCTS");
    }

    // 2. Check có đang là parent của category khác không
    boolean hasChildren = _categoryRepository.existsByParent_CategoryId(id);
    if (hasChildren) {
      throw new IllegalStateException("CATEGORY_HAS_CHILDREN");
    }

    _categoryRepository.delete(category);
  }
}
