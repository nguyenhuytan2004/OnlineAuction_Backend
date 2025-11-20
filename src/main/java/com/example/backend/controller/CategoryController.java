package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Category;
import com.example.backend.service.ICategoryService;

@RestController
@RequestMapping("api/categories")
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
