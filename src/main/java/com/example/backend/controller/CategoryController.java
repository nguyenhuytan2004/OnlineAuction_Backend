package com.example.backend.controller;

import com.example.backend.entity.Category;
import com.example.backend.model.Category.CategoryDetailResponse;
import com.example.backend.model.Category.CreateCategoryRequest;
import com.example.backend.model.Category.UpdateCategoryRequest;
import com.example.backend.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

  @Autowired
  private ICategoryService _categoryService;

  @GetMapping("")
  public ResponseEntity<?> getAllCategories() {
    try {
      List<Category> categories = _categoryService.getAllCategories();

      if (categories.isEmpty()) {
        log.warn(
                "[CONTROLLER][GET][CATEGORY] /api/categories - No categories found"
        );
        return new ResponseEntity<>("No categories found", HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(categories, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][GET][CATEGORY] /api/categories - Error occurred: {}",
              e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCategoryDetail(@PathVariable Integer id) {
    try {
      Category category = _categoryService.getCategoryById(id);

      if (category == null) {
        log.warn(
                "[CONTROLLER][GET][CATEGORY] /api/categories/{} - Category not found",
                id
        );
        return new ResponseEntity<>(
                "Category not found with ID: " + id,
                HttpStatus.NOT_FOUND
        );
      }

      return new ResponseEntity<>(
              CategoryDetailResponse.from(category),
              HttpStatus.OK
      );

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][GET][CATEGORY] /api/categories/{} - Error occurred: {}",
              id, e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @PostMapping("")
  public ResponseEntity<?> createCategory(
          @RequestBody CreateCategoryRequest createCategoryRequest) {

    try {
      Category created = _categoryService.createCategory(createCategoryRequest);
      return new ResponseEntity<>(created, HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.warn(
              "[CONTROLLER][POST][CATEGORY] /api/categories - Illegal argument: {}",
              iae.getMessage()
      );
      return new ResponseEntity<>(
              iae.getMessage(),
              HttpStatus.BAD_REQUEST
      );

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][POST][CATEGORY] /api/categories - Error occurred: {}",
              e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateCategory(
          @PathVariable("id") Integer id,
          @RequestBody UpdateCategoryRequest updateCategoryRequest) {

    try {
      Category updated =
              _categoryService.updateCategory(id, updateCategoryRequest);

      return new ResponseEntity<>(updated, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.warn(
              "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Illegal argument: {}",
              id, iae.getMessage()
      );
      return new ResponseEntity<>(
              iae.getMessage(),
              HttpStatus.BAD_REQUEST
      );

    } catch (RuntimeException re) {

      if ("CATEGORY_NOT_FOUND".equals(re.getMessage())) {
        log.warn(
                "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Category not found",
                id
        );
        return new ResponseEntity<>(
                "Category not found with id: " + id,
                HttpStatus.NOT_FOUND
        );
      }

      log.warn(
              "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Business error: {}",
              id, re.getMessage()
      );
      return new ResponseEntity<>(
              re.getMessage(),
              HttpStatus.BAD_REQUEST
      );

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Error occurred: {}",
              id, e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) {
    try {
      _categoryService.deleteCategory(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    } catch (RuntimeException ex) {

      switch (ex.getMessage()) {
        case "CATEGORY_NOT_FOUND" -> {
          log.warn(
                  "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Category not found",
                  id
          );
          return new ResponseEntity<>(
                  "Category not found with id: " + id,
                  HttpStatus.NOT_FOUND
          );
        }
        case "CATEGORY_HAS_PRODUCTS" -> {
          log.warn(
                  "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Category has products",
                  id
          );
          return new ResponseEntity<>(
                  "Cannot delete: category contains products",
                  HttpStatus.BAD_REQUEST
          );
        }
        case "CATEGORY_HAS_CHILDREN" -> {
          log.warn(
                  "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Category has children",
                  id
          );
          return new ResponseEntity<>(
                  "Cannot delete: category is parent of other categories",
                  HttpStatus.BAD_REQUEST
          );
        }
        default -> {
          log.warn(
                  "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Business error: {}",
                  id, ex.getMessage()
          );
          return new ResponseEntity<>(
                  ex.getMessage(),
                  HttpStatus.BAD_REQUEST
          );
        }
      }

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Error occurred: {}",
              id, e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }
}
