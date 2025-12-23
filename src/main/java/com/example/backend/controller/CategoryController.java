package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Category;
import com.example.backend.model.Category.CategoryDetailResponse;
import com.example.backend.model.Category.CreateCategoryRequest;
import com.example.backend.model.Category.UpdateCategoryRequest;
import com.example.backend.service.ICategoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/categories")
@Slf4j
public class CategoryController {

  @Autowired
  private ICategoryService _categoryService;

  @GetMapping("")
  public ResponseEntity<?> getAllCategories() {
    try {
      List<Category> categories = _categoryService.getAllCategories();

      if (categories.isEmpty()) {
        return new ResponseEntity<>("No categories found", HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(categories, HttpStatus.OK);

    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/categories - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCategoryDetail(@PathVariable Integer id) {
    try {
      Category category = _categoryService.getCategoryById(id);

      if (category == null) {
        return new ResponseEntity<>("Category not found with ID: " + id,
            HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(CategoryDetailResponse.from(category), HttpStatus.OK);

    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/categories/{} - {}", id, e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("")
  public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
    try {
      Category created = _categoryService.createCategory(createCategoryRequest);
      return new ResponseEntity<>(created, HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][POST][ERROR] /api/categories - Illegal argument: {}", iae.getMessage());
      return new ResponseEntity<>("Illegal argument: " + iae.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ERROR] /api/categories - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateCategory(
      @PathVariable("id") Integer id,
      @RequestBody UpdateCategoryRequest updateCategoryRequest) {

    try {
      Category updated = _categoryService.updateCategory(id, updateCategoryRequest);
      return new ResponseEntity<>(updated, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][PATCH][ERROR] /api/categories/{} - Illegal argument: {}", id, iae.getMessage());
      return new ResponseEntity<>("Illegal argument: " + iae.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (RuntimeException re) {
      log.error("[CONTROLLER][PATCH][ERROR] /api/categories/{} - {}", id, re.getMessage());

      if ("CATEGORY_NOT_FOUND".equals(re.getMessage())) {
        return new ResponseEntity<>("Category not found with id: " + id,
            HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>("Error occurred: " + re.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][PATCH][ERROR] /api/categories/{} - {}", id, e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) {
    try {
      _categoryService.deleteCategory(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    } catch (RuntimeException ex) {

      log.error("[CONTROLLER][DELETE][ERROR] /api/categories/{} - {}", id, ex.getMessage());

      return switch (ex.getMessage()) {
        case "CATEGORY_NOT_FOUND" -> new ResponseEntity<>("Category not found with id: " + id,
            HttpStatus.NOT_FOUND);
        case "CATEGORY_HAS_PRODUCTS" -> new ResponseEntity<>("Cannot delete: category contains products",
            HttpStatus.BAD_REQUEST);
        case "CATEGORY_HAS_CHILDREN" -> new ResponseEntity<>("Cannot delete: category is parent of other categories",
            HttpStatus.BAD_REQUEST);
        default -> new ResponseEntity<>("Error occurred: " + ex.getMessage(),
            HttpStatus.BAD_REQUEST);
      };

    } catch (Exception e) {
      log.error("[CONTROLLER][DELETE][ERROR] /api/categories/{} - {}", id, e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
