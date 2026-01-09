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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

  @Autowired
  private ICategoryService _categoryService;

  @Operation(summary = "Get all categories", description = "Retrieve a complete list of all product categories.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Category.class)))),
      @ApiResponse(responseCode = "404", description = "No categories found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("")
  public ResponseEntity<?> getAllCategories() {
    try {
      List<Category> categories = _categoryService.getAllCategories();

      if (categories.isEmpty()) {
        log.warn(
            "[CONTROLLER][GET][CATEGORY] /api/categories - No categories found");
        return new ResponseEntity<>("No categories found", HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(categories, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][CATEGORY] /api/categories - Error occurred: {}",
          e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get category details", description = "Retrieve detailed information about a specific category by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDetailResponse.class))),
      @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{id}")
  public ResponseEntity<?> getCategoryDetail(@PathVariable Integer id) {
    try {
      Category category = _categoryService.getCategoryById(id);

      if (category == null) {
        log.warn(
            "[CONTROLLER][GET][CATEGORY] /api/categories/{} - Category not found",
            id);
        return new ResponseEntity<>(
            "Category not found with ID: " + id,
            HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(
          CategoryDetailResponse.from(category),
          HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][CATEGORY] /api/categories/{} - Error occurred: {}",
          id, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Create a new category", description = "Create a new product category.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid category data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("")
  public ResponseEntity<?> createCategory(
      @RequestBody CreateCategoryRequest createCategoryRequest) {

    try {
      Category created = _categoryService.createCategory(createCategoryRequest);
      return new ResponseEntity<>(created, HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.warn(
          "[CONTROLLER][POST][CATEGORY] /api/categories - Illegal argument: {}",
          iae.getMessage());
      return new ResponseEntity<>(
          iae.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][POST][CATEGORY] /api/categories - Error occurred: {}",
          e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update category information", description = "Update an existing product category.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("/{id}")
  public ResponseEntity<?> updateCategory(
      @PathVariable("id") Integer id,
      @RequestBody UpdateCategoryRequest updateCategoryRequest) {

    try {
      Category updated = _categoryService.updateCategory(id, updateCategoryRequest);

      return new ResponseEntity<>(updated, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.warn(
          "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Illegal argument: {}",
          id, iae.getMessage());
      return new ResponseEntity<>(
          iae.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (RuntimeException re) {

      if ("CATEGORY_NOT_FOUND".equals(re.getMessage())) {
        log.warn(
            "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Category not found",
            id);
        return new ResponseEntity<>(
            "Category not found with id: " + id,
            HttpStatus.NOT_FOUND);
      }

      log.warn(
          "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Business error: {}",
          id, re.getMessage());
      return new ResponseEntity<>(
          re.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][PATCH][CATEGORY] /api/categories/{} - Error occurred: {}",
          id, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
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
              id);
          return new ResponseEntity<>(
              "Category not found with id: " + id,
              HttpStatus.NOT_FOUND);
        }
        case "CATEGORY_HAS_PRODUCTS" -> {
          log.warn(
              "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Category has products",
              id);
          return new ResponseEntity<>(
              "Cannot delete: category contains products",
              HttpStatus.BAD_REQUEST);
        }
        case "CATEGORY_HAS_CHILDREN" -> {
          log.warn(
              "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Category has children",
              id);
          return new ResponseEntity<>(
              "Cannot delete: category is parent of other categories",
              HttpStatus.BAD_REQUEST);
        }
        default -> {
          log.warn(
              "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Business error: {}",
              id, ex.getMessage());
          return new ResponseEntity<>(
              ex.getMessage(),
              HttpStatus.BAD_REQUEST);
        }
      }

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][DELETE][CATEGORY] /api/categories/{} - Error occurred: {}",
          id, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
