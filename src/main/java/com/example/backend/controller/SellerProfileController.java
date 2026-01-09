package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ISellerProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/user-profile")
public class SellerProfileController {
  @Autowired
  private ISellerProfileService _sellerProfileService;

  @Operation(summary = "Get seller's active products", description = "Retrieve all active auction products listed by the authenticated seller. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved active products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/active-products")
  public ResponseEntity<?> getActiveProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<Product> products = _sellerProfileService.getActiveProducts(userDetails.getUser().getUserId());

      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][ERROR] /api/user-profile/active-products - Error occurred: {}", e.getMessage(),
          e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get seller's sold products", description = "Retrieve all sold auction products from the authenticated seller. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved sold products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/sold-products")
  public ResponseEntity<?> getSoldProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<Product> products = _sellerProfileService.getSoldProducts(userDetails.getUser().getUserId());

      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][ERROR] /api/user-profile/sold-products - Error occurred: {}", e.getMessage(),
          e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
