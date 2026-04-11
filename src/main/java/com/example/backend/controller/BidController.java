package com.example.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Bid;
import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IBidService;
import com.example.backend.service.ISellerUpgradeRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/bids")
public class BidController {

  @Autowired
  private IBidService _bidService;

  @Autowired
  private ISellerUpgradeRequestService sellerUpgradeRequestService;

  @Operation(summary = "Get highest bidder for a product", description = "Retrieve the user with the highest bid for a specific product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Highest bidder found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "404", description = "No bids found for the product", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/product/{product_id}/highest-bidder")
  public ResponseEntity<?> getHighestBidderByProductId(@PathVariable("product_id") Integer productId) {
    try {
      User bidder = _bidService.getHighestBidderByProductId(productId);

      if (bidder == null) {
        log.warn(
            "[CONTROLLER][GET][WARN] /api/bids/product/{}/highest-bidder - No bids found for product ID: {}",
            productId, productId);
        return new ResponseEntity<>("No bids found for product ID: " + productId, HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(bidder, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/bids/product/{}/highest-bidder - An error occurred: {}",
          productId, e.getMessage());
      return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Place a bid on a product", description = "Create a new bid for an auction product. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Bid created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Bid.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - bid validation failed", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "409", description = "Concurrent bid conflict - refresh and retry", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("")
  public ResponseEntity<?> placeBid(@Valid @RequestBody CreateBidRequest createBidRequest) {
    try {
      Bid newBid = _bidService.placeBid(createBidRequest);

      return new ResponseEntity<>(newBid, HttpStatus.CREATED);
    } catch (IllegalArgumentException iae) {
      log.warn("[CONTROLLER][POST][WARN] /api/bids - Illegal argument: {}", iae.getMessage());

      return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ERROR] /api/bids - Error occurred: {}", e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get bid information", description = "Retrieve details of a specific bid by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bid found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Bid.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{bid_id}")
  public ResponseEntity<?> getBid(@PathVariable("bid_id") Integer bidId) {
    try {
      Bid bid = _bidService.getBid(bidId);

      return new ResponseEntity<>(bid, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/bids/{} - An error occurred: {}", bidId, e.getMessage());
      return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Create seller upgrade request", description = "Submit a request to upgrade a bidder account to a seller account. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Upgrade request created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SellerUpgradeRequest.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid state or conditions not met", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/seller-upgrade-request")
  public ResponseEntity<?> createSellerUpgradeRequest(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    try {
      Integer userId = userDetails.getUser().getUserId();

      SellerUpgradeRequest request = sellerUpgradeRequestService.createRequest(userId);

      return new ResponseEntity<>(request, HttpStatus.CREATED);

    } catch (IllegalStateException ise) {
      log.warn(
          "[CONTROLLER][POST][SELLER_UPGRADE] /api/seller-upgrade-request - Invalid state (userId={}): {}",
          userDetails.getUser().getUserId(),
          ise.getMessage());
      return new ResponseEntity<>(ise.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (IllegalArgumentException iae) {
      log.warn(
          "[CONTROLLER][POST][SELLER_UPGRADE] /api/seller-upgrade-request - Invalid argument (userId={}): {}",
          userDetails.getUser().getUserId(),
          iae.getMessage());
      return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][POST][SELLER_UPGRADE] /api/seller-upgrade-request - Error occurred (userId={}): {}",
          userDetails.getUser().getUserId(),
          e.getMessage(),
          e);
      return new ResponseEntity<>(
          "Failed to create seller upgrade request",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get seller upgrade request status", description = "Check the status of the authenticated user's latest seller upgrade request. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Status retrieved successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/seller-upgrade-request/status")
  public ResponseEntity<?> getSellerUpgradeRequestStatus(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      return sellerUpgradeRequestService
          .getLatestRequestByUser(userId)
          .map(req -> {
            Map<String, Object> body = new HashMap<>();
            body.put("hasRequest", true);
            body.put("status", req.getStatus());
            body.put("createdAt", req.getRequestAt());
            body.put("reviewedAt", req.getReviewedAt());
            body.put("comments", req.getComments());

            return ResponseEntity.ok(body);
          })
          .orElseGet(() -> {
            Map<String, Object> body = new HashMap<>();
            body.put("hasRequest", false);
            body.put("status", "NONE");
            return ResponseEntity.ok(body);
          });

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][SELLER_UPGRADE] /api/seller-upgrade-request/status - Error occurred (userId={}): {}",
          userId,
          e.getMessage(),
          e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
