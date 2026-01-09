package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.WatchList;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IWatchListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/watch-list")
@RequiredArgsConstructor
@Slf4j
public class WatchListController {

  private final IWatchListService _watchListService;

  @Operation(summary = "Check if product is in watch list", description = "Check if a product is in the authenticated user's watch list. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Check completed successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("{productId}")
  public ResponseEntity<?> isInWatchList(
      @PathVariable Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      boolean isInWatchList = _watchListService.isInWatchList(userId, productId);

      return new ResponseEntity<>(isInWatchList, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][GET][WATCH_LIST] /api/watch-list/{} - Error occurred (userId={}): {}",
          productId, userId, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Add product to watch list", description = "Add a product to the authenticated user's watch list. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Product added to watch list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WatchList.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid product or already in watch list", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("{productId}")
  public ResponseEntity<?> addToWatchList(
      @PathVariable Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      WatchList addedWatchList = _watchListService.addToWatchList(userId, productId);

      log.info(
          "[CONTROLLER][POST][WATCH_LIST] /api/watch-list/{} - Added to watch list (userId={})",
          productId, userId);

      return new ResponseEntity<>(addedWatchList, HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.warn(
          "[CONTROLLER][POST][WATCH_LIST] /api/watch-list/{} - Invalid request (userId={}): {}",
          productId, userId, iae.getMessage());
      return new ResponseEntity<>(
          iae.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][POST][WATCH_LIST] /api/watch-list/{} - Error occurred (userId={}): {}",
          productId, userId, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Remove product from watch list", description = "Remove a product from the authenticated user's watch list. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product removed from watch list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WatchList.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid product or not in watch list", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("{productId}")
  public ResponseEntity<?> removeFromWatchList(
      @PathVariable Integer productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      WatchList removedWatchList = _watchListService.removeFromWatchList(userId, productId);

      log.info(
          "[CONTROLLER][DELETE][WATCH_LIST] /api/watch-list/{} - Removed from watch list (userId={})",
          productId, userId);

      return new ResponseEntity<>(removedWatchList, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.warn(
          "[CONTROLLER][DELETE][WATCH_LIST] /api/watch-list/{} - Invalid request (userId={}): {}",
          productId, userId, iae.getMessage());
      return new ResponseEntity<>(
          iae.getMessage(),
          HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error(
          "[CONTROLLER][DELETE][WATCH_LIST] /api/watch-list/{} - Error occurred (userId={}): {}",
          productId, userId, e.getMessage(), e);
      return new ResponseEntity<>(
          "Internal server error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
