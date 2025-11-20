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

    @GetMapping("")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> productPage = _productService.getAllProducts();
            if (productPage.isEmpty()) {
                return new ResponseEntity<>(productPage, HttpStatus.OK);
            }
            return new ResponseEntity<>(productPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{product_id}")
    public ResponseEntity<?> getProductById(@PathVariable("product_id") Integer productId) {
        try {
            Product product = _productService.getProductById(productId);
            if (product == null) {
                return new ResponseEntity<>("Product not found with ID: " + productId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("category/{category_id}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("category_id") Integer categoryId,
            Pageable pageable) {
        try {
            Page<Product> productPage = _productService.getProductsByCategoryId(categoryId, pageable);
            if (productPage.getContent().isEmpty()) {
                return new ResponseEntity<>("No products found for category: " + categoryId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("top-5-ending-soon")
    public ResponseEntity<?> getTop5EndingSoonProducts() {
        try {
            List<Product> products = _productService.getTop5EndingSoonProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("top-5-most-auctioned")
    public ResponseEntity<?> getTop5MostAuctionedProducts() {
        try {
            List<Product> products = _productService.getTop5MostAuctionedProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("top-5-highest-priced")
    public ResponseEntity<?> getTop5HighestPricedProducts() {
        try {
            List<Product> products = _productService.getTop5HighestPricedProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{product_id}/top-5-related")
    public ResponseEntity<?> getTop5RelatedProducts(@PathVariable("product_id") Integer productId) {
        try {
            Product product = _productService.getProductById(productId);
            if (product == null) {
                return new ResponseEntity<>("Product not found with ID: " + productId, HttpStatus.NOT_FOUND);
            }
            Integer categoryId = product.getCategory().getCategoryId();
            List<Product> relatedProducts = _productService.getTop5RelatedProducts(categoryId, productId);
            return new ResponseEntity<>(relatedProducts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}