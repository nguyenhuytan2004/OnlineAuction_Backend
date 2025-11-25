package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.model.Product.CreateProductRequest;
import com.example.backend.service.IProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/products")
public class ProductController {
    @Autowired
    private IProductService _productService;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ProductController.class);

    @GetMapping("")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = _productService.getAllProducts();
            if (products.isEmpty()) {
                return new ResponseEntity<>(products, HttpStatus.OK);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
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
                return new ResponseEntity<>(
                        "No products found for category ID " + categoryId + " and criteria " + pageable.toString(),
                        HttpStatus.NOT_FOUND);
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

    @GetMapping("full-text-search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword,
            Pageable pageable) {
        try {
            Page<Product> productPage = _productService.searchProducts(keyword, null, pageable);
            if (productPage.getContent().isEmpty()) {
                LOGGER.warn(
                        "[CONTROLLER][GET][WARN] /api/products/full-text-search - No products found for keyword: \"{}\" and criteria: \"{}\"",
                        keyword, pageable.toString());
                return new ResponseEntity<>(
                        "No products found for keyword " + keyword + " and criteria " + pageable.toString(),
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productPage, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][GET][ERROR] /api/products/full-text-search - Error occurred: {}", e.getMessage(),
                    e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("category/{category_id}/full-text-search")
    public ResponseEntity<?> searchProducts(@PathVariable(value = "category_id") Integer categoryId,
            @RequestParam String keyword,
            Pageable pageable) {
        try {
            Page<Product> productPage = _productService.searchProducts(keyword, categoryId, pageable);
            if (productPage.getContent().isEmpty()) {
                return new ResponseEntity<>("No products found for category ID " + categoryId + " with keyword "
                        + keyword + " and criteria " + pageable.toString(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request,
            @RequestParam Integer sellerId) {
        try {
            Product createdProduct = _productService.createProduct(request, sellerId);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][POST][ERROR] /api/products - Error occurred: {}", e.getMessage(), e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // buy-now endpoint
    @PatchMapping("{product_id}/buy-now")
    public ResponseEntity<?> buyNowProduct(@PathVariable("product_id") Integer productId,
            @RequestParam Integer buyerId) {
        try {
            AuctionResult auctionResult = _productService.buyNowProduct(productId, buyerId);
            if (auctionResult != null) {
                return new ResponseEntity<>(auctionResult, HttpStatus.OK);
            } else {
                LOGGER.warn("[CONTROLLER][PATCH][ERROR] /api/products/{} - Failed to buy the product immediately.",
                        productId);
                return new ResponseEntity<>("Failed to buy the product immediately.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][PATCH][ERROR] /api/products/{} - Error occurred: {}", productId, e.getMessage(),
                    e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}