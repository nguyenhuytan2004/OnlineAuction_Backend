package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.model.CreateBlockedBidderRequest;
import com.example.backend.model.Product.AppendDescriptionRequest;
import com.example.backend.model.Product.CreateProductRequest;
import com.example.backend.model.Product.UpdateProductRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IBidService;
import com.example.backend.service.IBlockedBidderService;
import com.example.backend.service.IOrderService;
import com.example.backend.service.IProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/products")
public class ProductController {

  @Autowired
  private IProductService _productService;
  @Autowired
  private IBidService _bidService;
  @Autowired
  private IBlockedBidderService _blockedBidderService;
  @Autowired
  private IOrderService _orderService;

  @Operation(summary = "Get products with optional status filter", description = "Retrieve paginated list of products, optionally filtered by status.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid status", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("")
  public ResponseEntity<?> getProductsWithStatus(@RequestParam(required = false) String status, Pageable pageable) {
    try {
      Page<Product> products = _productService.getProducts(status, pageable);
      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][GET][PRODUCT] /api/products?status={} - Illegal argument: {}", status,
          iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][PRODUCT] /api/products?status={} - Exception: {}", status, e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get product by ID", description = "Retrieve detailed information of a specific product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{product_id}")
  public ResponseEntity<?> getProduct(@PathVariable("product_id") Integer productId) {
    try {
      Product product = _productService.getProduct(productId);

      if (product == null) {
        log.warn(
            "[CONTROLLER][GET][PRODUCT] /api/products/{} - Product not found",
            productId);
        return new ResponseEntity<>(
            "Product not found with ID: " + productId,
            HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(product, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/{} - Error occurred: {}",
          productId, e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get products by category", description = "Retrieve paginated list of products filtered by category ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid category ID", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("category/{category_id}")
  public ResponseEntity<?> getProductsByCategoryId(
      @PathVariable("category_id") Integer categoryId,
      Pageable pageable) {

    try {
      Page<Product> productPage = _productService.getProductsByCategoryId(categoryId, pageable);

      return new ResponseEntity<>(productPage, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/category/{} - Illegal argument: {}",
          categoryId, iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/category/{} - Error occurred: {}",
          categoryId, e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get top 5 products ending soon", description = "Retrieve the 5 products with the earliest auction end times.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("top-5-ending-soon")
  public ResponseEntity<?> getTop5EndingSoonProducts() {
    try {
      List<Product> products = _productService.getTop5EndingSoonProducts();
      return new ResponseEntity<>(products, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/top-5-ending-soon - Error occurred: {}",
          e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get top 5 most auctioned products", description = "Retrieve the 5 products with the highest number of bids.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("top-5-most-auctioned")
  public ResponseEntity<?> getTop5MostAuctionedProducts() {
    try {
      List<Product> products = _productService.getTop5MostAuctionedProducts();
      return new ResponseEntity<>(products, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/top-5-most-auctioned - Error occurred: {}",
          e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get top 5 highest priced products", description = "Retrieve the 5 products with the highest prices.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("top-5-highest-priced")
  public ResponseEntity<?> getTop5HighestPricedProducts() {
    try {
      List<Product> products = _productService.getTop5HighestPricedProducts();
      return new ResponseEntity<>(products, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/top-5-highest-priced - Error occurred: {}",
          e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get top 5 related products", description = "Retrieve 5 products in the same category as the specified product, excluding the product itself.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved related products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{product_id}/top-5-related")
  public ResponseEntity<?> getTop5RelatedProducts(
      @PathVariable("product_id") Integer productId) {

    try {
      Product product = _productService.getProduct(productId);

      if (product == null) {
        log.warn(
            "[CONTROLLER][GET][PRODUCT] /api/products/{}/top-5-related - Product not found",
            productId);
        return new ResponseEntity<>(
            "Product not found with ID: " + productId,
            HttpStatus.NOT_FOUND);
      }

      Integer categoryId = product.getCategory().getCategoryId();

      List<Product> relatedProducts = _productService.getTop5RelatedProducts(categoryId, productId);

      return new ResponseEntity<>(relatedProducts, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/{}/top-5-related - Error occurred: {}",
          productId, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Search products by keyword", description = "Search products using full-text search across all products.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("full-text-search")
  public ResponseEntity<?> searchProducts(@RequestParam String keyword,
      Pageable pageable) {
    try {
      Page<Product> productPage = _productService.searchProducts(keyword, null, pageable);

      return new ResponseEntity<>(productPage, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][PRODUCT] /api/products/full-text-search - Error occurred: {}", e.getMessage(),
          e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Search products by category and keyword", description = "Search products using full-text search filtered by category.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid category ID", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("category/{category_id}/full-text-search")
  public ResponseEntity<?> searchProducts(
      @PathVariable("category_id") Integer categoryId,
      @RequestParam String keyword,
      Pageable pageable) {

    try {
      Page<Product> productPage = _productService.searchProducts(keyword, categoryId, pageable);

      return new ResponseEntity<>(productPage, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/category/{}/full-text-search - Illegal argument (keyword={}): {}",
          categoryId, keyword, iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/category/{}/full-text-search - Error occurred (keyword={}): {}",
          categoryId, keyword, e.getMessage(), e);
      return new ResponseEntity<>("Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Create a new product", description = "Create a new auction product. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid product data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("")
  public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      Product createdProduct = _productService.createProduct(request,
          userDetails.getUser().getUserId());
      return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][POST][PRODUCT] /api/products - Illegal argument: {}", iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][PRODUCT] /api/products - Error occurred: {}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Buy now (instant purchase)", description = "Purchase a product instantly without bidding. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Purchase successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionResult.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - product not available for purchase", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("{product_id}/buy-now")
  public ResponseEntity<?> buyNowProduct(@PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      AuctionResult auctionResult = _productService.buyNowProduct(productId, userDetails.getUser().getUserId());
      return new ResponseEntity<>(auctionResult, HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][PATCH][PRODUCT] /api/products/{} - Illegal argument: {}", productId,
          iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][PATCH][PRODUCT] /api/products/{} - Error occurred: {}", productId, e.getMessage(),
          e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Append description to product", description = "Add additional description to an existing product. Only the seller can do this. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Description updated successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("{product_id}/append-description")
  public ResponseEntity<?> appendDescription(@PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody AppendDescriptionRequest request) {
    try {
      String updatedDescription = _productService.appendDescription(userDetails.getUser().getUserId(), productId,
          request.getAdditionalDescription());
      return new ResponseEntity<>(updatedDescription, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][PATCH][PRODUCT] /api/products/{}/append-description - Error occurred: {}",
          productId,
          e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Check bidding eligibility", description = "Check if the authenticated user is eligible to bid on a product. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Eligibility check completed", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{product_id}/bidding-eligibility")
  public ResponseEntity<?> checkBiddingEligibility(@PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      boolean isEligible = _productService.checkBiddingEligibility(productId, userDetails.getUser().getUserId());

      return new ResponseEntity<>(isEligible, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][PRODUCT] /api/products/{}/bidding-eligibility - Error occurred: {}",
          productId, e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get top 5 bids for a product", description = "Retrieve the 5 highest bids for a specific product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved bids", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Bid.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{product_id}/bids")
  public ResponseEntity<?> getTop5Bids(@PathVariable("product_id") Integer productId) {
    try {
      List<Bid> bids = _bidService.getTop5BidsByProductId(productId);
      return new ResponseEntity<>(bids, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][PRODUCT] /api/products/{}/bids - Error occurred: {}", productId,
          e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Delete a product", description = "Delete a product from the auction. Only the seller can delete their own products. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Product deleted successfully", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request - cannot delete", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("{product_id}")
  public ResponseEntity<?> deleteProduct(
      @PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      Integer requesterId = userDetails.getUser().getUserId();

      _productService.deleteProduct(productId, requesterId);

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][DELETE][PRODUCT] /api/products/{} - Illegal argument: {}", productId,
          iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][DELETE][PRODUCT] /api/products/{} - Error occurred: {}", productId,
          e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Check if user is bid-blocked", description = "Inspect whether the authenticated user is blocked from bidding on a product. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bid-blocking status retrieved", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{product_id}/bid-blocking-inspection")
  public ResponseEntity<?> inspectBidBlocking(
      @PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      Integer userId = userDetails.getUser().getUserId();

      Boolean isBlocked = _blockedBidderService.checkBidderBlocked(productId, userId);

      return new ResponseEntity<>(isBlocked, HttpStatus.OK);

    } catch (Exception e) {
      log.error("[CONTROLLER][GET][PRODUCT] /api/products/{}/bid-blocking-inspection - Error occurred: {}",
          productId, e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Block a bidder from product", description = "Block a user from bidding on a product. Only the seller can block bidders. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Bidder blocked successfully", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("{product_id}/block-bidder")
  public ResponseEntity<?> blockBidder(
      @PathVariable("product_id") Integer productId,
      @RequestBody CreateBlockedBidderRequest createBlockedBidderRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      Integer blockerId = userDetails.getUser().getUserId();

      _productService.blockBidder(productId, blockerId, createBlockedBidderRequest.getBlockedId(),
          createBlockedBidderRequest.getReason());

      return new ResponseEntity<>(HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][POST][PRODUCT] /api/products/{}/block-bidder - Illegal argument: {}",
          productId, iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][POST][PRODUCT] /api/products/{}/block-bidder - Error occurred: {}",
          productId, e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update product information", description = "Update an existing product's information. Only the seller can update their own products.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("/{product_id}")
  public ResponseEntity<?> updateProduct(
      @PathVariable("product_id") Integer productId,
      @RequestBody @Valid UpdateProductRequest updateProductRequest) {
    try {
      Product updatedProduct = _productService.updateProduct(productId, updateProductRequest);
      return new ResponseEntity<>(updatedProduct, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][PATCH][PRODUCT] /api/products/{} - Illegal argument: {}", productId,
          iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][PATCH][PRODUCT] /api/products/{} - Error occurred: {}", productId,
          e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get auction order for product", description = "Retrieve the auction order associated with a specific product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Auction order retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionOrder.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{product_id}/auction-order")
  public ResponseEntity<?> getAuctionOrderByProductId(@PathVariable("product_id") Integer productId) {
    try {
      AuctionOrder auctionOrder = _orderService.getAuctionOrderByProductId(productId);

      return new ResponseEntity<>(auctionOrder, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][PRODUCT] /api/products/{}/auction-order - Internal server error: {}", productId,
          e.getMessage(), e);

      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
