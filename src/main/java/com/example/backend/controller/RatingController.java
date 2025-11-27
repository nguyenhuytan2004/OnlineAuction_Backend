package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Rating;
import com.example.backend.model.Rating.CreateRatingRequest;
import com.example.backend.service.IRatingService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/ratings")
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @PostMapping("/seller")
    public ResponseEntity<?> rateSeller(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateRatingRequest createRatingRequest) {
        try {
            Rating newRating = ratingService.rateSeller(createRatingRequest, userId);

            return new ResponseEntity<>(newRating, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            log.info("[CONTROLLER][POST][WARN] /api/ratings/{} - Illegal argument: {}",
                    userId, e.getMessage());
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[CONTROLLER][POST][ERROR] /api/ratings/{} - Error creating rating: {}",
                    userId, e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/buyer")
    public ResponseEntity<?> rateBuyer(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateRatingRequest createRatingRequest) {
        try {
            Rating newRating = ratingService.rateBuyer(createRatingRequest, userId);

            return new ResponseEntity<>(newRating, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.info("[CONTROLLER][POST][WARN] /api/ratings/{} - Illegal argument: {}",
                    userId, e.getMessage());
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[CONTROLLER][POST][ERROR] /api/ratings/{} - Error creating rating: {}",
                    userId, e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
