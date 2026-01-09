package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.backend.entity.Rating;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.model.Rating.UpdateRatingRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IRatingService;

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
@RequestMapping("api/ratings")
public class RatingController {

  @Autowired
  private IRatingService _ratingService;

  @Operation(summary = "Check if user has rated", description = "Check if a specific reviewer has rated a specific reviewee for a product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Check completed successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("check-if-rated")
  public ResponseEntity<?> checkIfRated(@RequestParam Integer productId,
      @RequestParam Integer reviewerId,
      @RequestParam Integer revieweeId) {
    try {
      Boolean isRated = _ratingService.checkIfRated(
          productId,
          reviewerId,
          revieweeId);

      return new ResponseEntity<>(isRated, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/ratings/check-if-rated - Error fetching ratings: {}",
          e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get specific rating", description = "Retrieve a specific rating between a reviewer and reviewee for a product.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Rating found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  public ResponseEntity<?> getRating(@RequestParam Integer productId,
      @RequestParam Integer reviewerId,
      @RequestParam Integer revieweeId) {
    try {
      Rating rating = _ratingService.getRating(
          productId,
          reviewerId,
          revieweeId);

      return new ResponseEntity<>(rating, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/ratings - Error fetching rating: {}",
          e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get ratings for a user", description = "Retrieve all ratings received by a specific user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ratings retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Rating.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/reviewee/{revieweeId}")
  public ResponseEntity<?> getRatingsByReviewee(@PathVariable Integer revieweeId) {
    try {
      List<Rating> ratings = _ratingService.getRatingsByRevieweeId(revieweeId);

      return new ResponseEntity<>(ratings, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/ratings/user/{} - Error fetching user ratings: {}",
          revieweeId, e.getMessage(), e);

      return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Rate a seller", description = "Create a new rating for a seller. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Rating created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid rating data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/seller")
  public ResponseEntity<?> rateSeller(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody CreateRatingRequest createRatingRequest) {
    try {
      Rating newRating = _ratingService.rateSeller(createRatingRequest, userDetails.getUser().getUserId());

      return new ResponseEntity<>(newRating, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      log.info("[CONTROLLER][POST][WARN] /api/ratings/{} - Illegal argument: {}",
          userDetails.getUser().getUserId(), e.getMessage());

      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ERROR] /api/ratings/{} - Error creating rating: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Rate a buyer", description = "Create a new rating for a buyer. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Rating created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid rating data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/buyer")
  public ResponseEntity<?> rateBuyer(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody CreateRatingRequest createRatingRequest) {
    try {
      Rating newRating = _ratingService.rateBuyer(createRatingRequest, userDetails.getUser().getUserId());

      return new ResponseEntity<>(newRating, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      log.info("[CONTROLLER][POST][WARN] /api/ratings/{} - Illegal argument: {}",
          userDetails.getUser().getUserId(), e.getMessage());

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ERROR] /api/ratings/{} - Error creating rating: {}",
          userDetails.getUser().getUserId(), e.getMessage(), e);

      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update a rating", description = "Update an existing rating. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Rating updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("")
  public ResponseEntity<?> updateRating(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody UpdateRatingRequest updateRatingRequest) {
    try {
      Rating updatedRating = _ratingService.updateRating(updateRatingRequest, userDetails.getUser().getUserId());

      return new ResponseEntity<>(updatedRating, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      log.info("[CONTROLLER][PATCH][WARN] /api/ratings - Illegal argument: {}",
          e.getMessage());
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][PATCH][ERROR] /api/ratings - Error updating rating: {}",
          e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
