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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auction-results")
public class AuctionResultController {
  @Autowired
  private IAuctionResultService _auctionResultService;

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

  @PatchMapping("/product/{product_id}/cancel")
  public ResponseEntity<?> cancelAuction(@PathVariable("product_id") Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      AuctionResult cancelledAuction = _auctionResultService.cancelAuction(productId,
          userDetails.getUser().getUserId());

      return new ResponseEntity<>(cancelledAuction, HttpStatus.OK);
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
