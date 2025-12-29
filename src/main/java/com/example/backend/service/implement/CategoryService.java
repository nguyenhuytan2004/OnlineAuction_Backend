package com.example.backend.service.implement;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Category;
import com.example.backend.model.Category.CreateCategoryRequest;
import com.example.backend.model.Category.UpdateCategoryRequest;
import com.example.backend.repository.ICategoryRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.ICategoryService;

@Service
@Slf4j
public class CategoryService implements ICategoryService {

  @Autowired
  private ICategoryRepository _categoryRepository;
  @Autowired
  private IProductRepository _productRepository;

  @Override
  public List<Category> getAllCategories() {
    log.info(
            "[SERVICE][GET][CATEGORIES] Input"
    );

    try {
      List<Category> categories = _categoryRepository.findAll();

      log.info(
              "[SERVICE][GET][CATEGORIES] Output categories={}",
              categories
      );
      return categories;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][CATEGORIES] Error occurred: {}",
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public Category createCategory(CreateCategoryRequest createCategoryRequest) {
    log.info(
            "[SERVICE][POST][CREATE_CATEGORY] Input request={}",
            createCategoryRequest
    );

    try {
      Category newCategory = new Category();
      newCategory.setCategoryName(createCategoryRequest.getCategoryName());
      newCategory.setDescription(createCategoryRequest.getDescription());

      if (createCategoryRequest.getParentCategoryId() != null) {
        Category parent = _categoryRepository
                .findById(createCategoryRequest.getParentCategoryId())
                .orElseThrow(() -> new RuntimeException("Parent category not found"));
        newCategory.setParent(parent);
      } else {
        newCategory.setParent(null);
      }

      Category saved = _categoryRepository.save(newCategory);

      log.info(
              "[SERVICE][POST][CREATE_CATEGORY] Output category={}",
              saved
      );
      return saved;

    } catch (Exception e) {
      log.error(
              "[SERVICE][POST][CREATE_CATEGORY] Error occurred (request={}): {}",
              createCategoryRequest,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public Category updateCategory(Integer id, UpdateCategoryRequest updateCategoryRequest) {
    log.info(
            "[SERVICE][PUT][UPDATE_CATEGORY] Input id={}, request={}",
            id,
            updateCategoryRequest
    );

    try {
      Category existing = _categoryRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

      existing.setCategoryName(updateCategoryRequest.getCategoryName());
      existing.setDescription(updateCategoryRequest.getDescription());

      if (updateCategoryRequest.getParentCategoryId() != null) {
        if (Objects.equals(updateCategoryRequest.getParentCategoryId(), id)) {
          throw new RuntimeException("Category cannot be parent of itself");
        }

        Category parent = _categoryRepository
                .findById(updateCategoryRequest.getParentCategoryId())
                .orElseThrow(() -> new RuntimeException("Parent category not found"));
        existing.setParent(parent);
      } else {
        existing.setParent(null);
      }

      Category saved = _categoryRepository.save(existing);

      log.info(
              "[SERVICE][PUT][UPDATE_CATEGORY] Output category={}",
              saved
      );
      return saved;

    } catch (Exception e) {
      log.error(
              "[SERVICE][PUT][UPDATE_CATEGORY] Error occurred (id={}): {}",
              id,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public Category getCategoryById(Integer id) {
    log.info(
            "[SERVICE][GET][CATEGORY] Input id={}",
            id
    );

    try {
      Category category = _categoryRepository.findById(id).orElse(null);

      log.info(
              "[SERVICE][GET][CATEGORY] Output category={}",
              category
      );
      return category;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][CATEGORY] Error occurred (id={}): {}",
              id,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public void deleteCategory(Integer id) {
    log.info(
            "[SERVICE][DELETE][CATEGORY] Input id={}",
            id
    );

    try {
      Category category = _categoryRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

      boolean hasProducts =
              _productRepository.existsByCategory_CategoryId(id);
      if (hasProducts) {
        throw new IllegalStateException("CATEGORY_HAS_PRODUCTS");
      }

      boolean hasChildren =
              _categoryRepository.existsByParent_CategoryId(id);
      if (hasChildren) {
        throw new IllegalStateException("CATEGORY_HAS_CHILDREN");
      }

      _categoryRepository.delete(category);

      log.info(
              "[SERVICE][DELETE][CATEGORY] Success id={}",
              id
      );

    } catch (Exception e) {
      log.error(
              "[SERVICE][DELETE][CATEGORY] Error occurred (id={}): {}",
              id,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
