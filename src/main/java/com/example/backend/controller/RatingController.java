package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/ratings")
public class RatingController {

    @Autowired
    private IRatingService _ratingService;

    @GetMapping("check-if-rated")
    public ResponseEntity<?> checkIfRated(@RequestParam Integer productId,
            @RequestParam Integer reviewerId, @RequestParam Integer revieweeId) {
        try {
            Boolean isRated = _ratingService.checkIfRated(productId, reviewerId, revieweeId);

            return new ResponseEntity<>(isRated, HttpStatus.OK);
        } catch (Exception e) {
            log.error("[CONTROLLER][GET][ERROR] /api/ratings/check-if-rated - Error fetching ratings: {}",
                    e.getMessage(), e);

            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("check-seller-rated-buyer")
    public ResponseEntity<?> checkIfSellerRatedBuyer(@RequestParam Integer sellerId,
            @RequestParam Integer buyerId) {
        try {
            List<Boolean> isRatedList = _ratingService.checkIfSellerRatedBuyer(sellerId, buyerId);

            return new ResponseEntity<>(isRatedList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("[CONTROLLER][POST][ERROR] /api/ratings/check-seller-rated-buyer - Error fetching rating: {}",
                    e.getMessage(), e);

            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @PatchMapping("")
    public ResponseEntity<?> updateRating(
            @RequestParam Integer userId,
            @Valid @RequestBody UpdateRatingRequest updateRatingRequest) {
        try {
            Rating updatedRating = _ratingService.updateRating(updateRatingRequest, userId);

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
