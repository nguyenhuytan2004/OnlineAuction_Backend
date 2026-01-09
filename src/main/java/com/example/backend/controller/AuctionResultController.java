package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.AuctionResult;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IAuctionResultService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auction-results")
public class AuctionResultController {
  @Autowired
  private IAuctionResultService _auctionResultService;

  @Operation(summary = "Get auction result", description = "Retrieve the auction result for a specific product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Auction result retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionResult.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("product/{product_id}")
  public ResponseEntity<?> getAuctionResult(@PathVariable("product_id") Integer productId) {
    try {
      AuctionResult auctionResult = _auctionResultService.getAuctionResult(productId);

      return new ResponseEntity<>(auctionResult, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/auction-results - Error fetching auction result: {}",
          e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Cancel auction", description = "Cancel an auction for a product. Only the seller can cancel. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Auction cancelled", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionResult.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - cannot cancel auction", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("/product/{product_id}/cancel")
  public ResponseEntity<?> cancelAuction(@PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      AuctionResult canceledAuction = _auctionResultService.cancelAuction(productId,
          userDetails.getUser().getUserId());

      return new ResponseEntity<>(canceledAuction, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      log.info("[CONTROLLER][PATCH][WARN] /api/auction-results/product/{}/cancel - Illegal argument: {}",
          productId, e.getMessage());

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(
          "[CONTROLLER][PATCH][ERROR] /api/auction-results/product/{}/cancel - Error cancelling auction: {}",
          productId, e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
