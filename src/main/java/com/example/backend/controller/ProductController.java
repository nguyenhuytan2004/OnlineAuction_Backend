package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.service.IProductService;

@RestController
@RequestMapping("api/products")
public class ProductController {
    @Autowired
    private IProductService _productService;

    @GetMapping("category/{category_id}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("category_id") Integer categoryId,
            Pageable pageable) {
        try {
            Page<Product> productPage = _productService.getProductsByCategoryId(categoryId, pageable);
            if (productPage.getContent().isEmpty()) {
                return new ResponseEntity<>("No products found for category: " + categoryId, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(productPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("ending_soon")
    public ResponseEntity<?> getTop5ProductsEndingSoon() {
        try {
            List<Product> products = _productService.getTop5ProductsEndingSoon();
            if (products.isEmpty()) {
                return new ResponseEntity<>("No products found", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}