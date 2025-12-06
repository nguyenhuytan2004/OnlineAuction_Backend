package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.model.Product.AppendDescriptionRequest;
import com.example.backend.model.Product.CreateProductRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IBidService;
import com.example.backend.service.IProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private IProductService _productService;
    @Autowired
    private IBidService _bidService;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ProductController.class);

    @GetMapping("")
    public ResponseEntity<?> getAllProducts(Pageable pageable) {
        try {
            Page<Product> productPage = _productService.getAllProducts(pageable);
            return new ResponseEntity<>(productPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{product_id}")
    public ResponseEntity<?> getProduct(@PathVariable("product_id") Integer productId) {
        try {
            Product product = _productService.getProduct(productId);
            if (product == null) {
                return new ResponseEntity<>("Product not found with ID: " + productId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("category/{category_id}")
    public ResponseEntity<?> getProductsByCategoryId(@PathVariable("category_id") Integer categoryId,
            Pageable pageable) {
        try {
            Page<Product> productPage = _productService.getProductsByCategoryId(categoryId, pageable);
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
            Product product = _productService.getProduct(productId);
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
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("{product_id}/buy-now")
    public ResponseEntity<?> buyNowProduct(@PathVariable("product_id") Integer productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AuctionResult auctionResult = _productService.buyNowProduct(productId, userDetails.getUser().getUserId());
            return new ResponseEntity<>(auctionResult, HttpStatus.OK);
        } catch (IllegalArgumentException iae) {
            LOGGER.error("[CONTROLLER][PATCH][ERROR] /api/products/{} - Illegal argument: {}", productId,
                    iae.getMessage());
            return new ResponseEntity<>("Illegal argument: " + iae.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][PATCH][ERROR] /api/products/{} - Error occurred: {}", productId, e.getMessage(),
                    e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("{product_id}/append-description")
    public ResponseEntity<?> appendDescription(@PathVariable("product_id") Integer productId,
            @RequestParam Integer userId,
            @Valid @RequestBody AppendDescriptionRequest request) {
        try {
            String updatedDescription = _productService.appendDescription(userId, productId,
                    request.getAdditionalDescription());
            return new ResponseEntity<>(updatedDescription, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][PATCH][ERROR] /api/products/{}/append-description - Error occurred: {}",
                    productId,
                    e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{product_id}/bidding-eligibility")
    public ResponseEntity<?> checkBiddingEligibility(@PathVariable("product_id") Integer productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isEligible = _productService.checkBiddingEligibility(productId, userDetails.getUser().getUserId());

            return new ResponseEntity<>(isEligible, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(
                    "[CONTROLLER][GET][ERROR] /api/products/{}/bidding-eligibility - Error occurred: {}",
                    productId, e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{product_id}/bids")
    public ResponseEntity<?> getTop5Bids(@PathVariable("product_id") Integer productId) {
        try {
            List<Bid> bids = _bidService.getTop5BidsByProductId(productId);
            return new ResponseEntity<>(bids, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][GET][ERROR] /api/products/{}/bids - Error occurred: {}", productId,
                    e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
