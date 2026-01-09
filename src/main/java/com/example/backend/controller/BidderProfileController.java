package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.entity.Rating;
import com.example.backend.entity.WatchList;
import com.example.backend.model.BidderProfile.UpdatePasswordRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IBidderProfileService;
import com.example.backend.service.IProductService;
import com.example.backend.service.IRatingService;
import com.example.backend.service.IWatchListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/user-profile")
public class BidderProfileController {
  @Autowired
  private IBidderProfileService _bidderProfileService;
  @Autowired
  private IWatchListService _watchListService;
  @Autowired
  private IProductService _productService;
  @Autowired
  private IRatingService _ratingService;

  @Operation(summary = "Get ratings for the authenticated user", description = "Retrieve all ratings received by the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved ratings", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Rating.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/ratings")
  public ResponseEntity<?> getRatings(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<Rating> ratings = _ratingService.getRatingsByRevieweeId(userDetails.getUser().getUserId());
      return new ResponseEntity<>(ratings, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][ERROR] /api/bidder-profile/rating - Error occurred for user with ID: {}: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get watch list for the authenticated user", description = "Retrieve all products in the watch list of the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved watch list", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/watch-list")
  public ResponseEntity<?> getWatchListByUserId(@AuthenticationPrincipal CustomUserDetails userDetails) {

    try {
      List<WatchList> watchLists = _watchListService.getWatchList(userDetails.getUser().getUserId());

      List<Product> products = new ArrayList<>();
      for (WatchList watchList : watchLists) {
        Product product = _productService.getProduct(watchList.getProduct().getProductId());
        if (product != null) {
          products.add(product);
        }
      }
      return new ResponseEntity<>(products, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][ERROR] /api/user-profile/watch-list - Error occurred for user with ID: {}: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);

      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get products the user is participating in", description = "Retrieve all products that the authenticated user is currently bidding on.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved participating products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/participating-products")
  public ResponseEntity<?> getParticipatingProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<Product> products = _bidderProfileService.getParticipatingProducts(userDetails.getUser().getUserId());
      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][ERROR] /api/bidder-profile/participating-products - Error occurred for user with ID: {}: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get products won by the authenticated user", description = "Retrieve all products that the authenticated user has won.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved won products", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/won-products")
  public ResponseEntity<?> getWonProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<Product> products = _bidderProfileService.getWonProducts(userDetails.getUser().getUserId());

      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][ERROR] /api/bidder-profile/won-products - Error occurred for user with ID: {}: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Change password for the authenticated user", description = "Update the password of the authenticated user. Requires current password and confirmation of new password.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid password or mismatch", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("")
  public ResponseEntity<?> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
    try {
      _bidderProfileService.changePassword(userDetails.getUser().getUserId(),
          updatePasswordRequest.getCurrentPassword(),
          updatePasswordRequest.getNewPassword(), updatePasswordRequest.getConfirmPassword());

      return new ResponseEntity<>("Đổi mật khẩu thành công", HttpStatus.OK);
    } catch (RuntimeException e) {
      log.warn(
          "[CONTROLLER][PATCH][WARN] /api/bidder-profile/password - Error occurred for user with ID: {}: {}",
          userDetails.getUser().getUserId(), e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][PATCH][ERROR] /api/bidder-profile/password - Unexpected error occurred for user with ID: {}: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
