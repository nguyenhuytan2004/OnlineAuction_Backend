package com.example.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.service.IBidderProfileService;

@RestController
@RequestMapping("api/bidder-profile")
public class BidderProfileController {
    @Autowired
    private IBidderProfileService _bidderProfileService;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BidderProfileController.class);

    @GetMapping("/participating-products")
    public ResponseEntity<?> getParticipatingProducts(@RequestParam Integer userId) {
        try {
            List<Product> products = _bidderProfileService.getParticipatingProducts(userId);
            if (products.isEmpty()) {
                LOGGER.info(
                        "[CONTROLLER][GET][WARN] /api/profile/participating-products - No participating products found for user with ID: {}",
                        userId);
                return new ResponseEntity<>(products, HttpStatus.OK);
            }

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(
                    "[CONTROLLER][GET][ERROR] /api/profile/participating-products - Error occurred for user with ID: {}: {}",
                    userId, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/won-products")
    public ResponseEntity<?> getWonProducts(@RequestParam Integer userId) {
        try {
            List<Product> products = _bidderProfileService.getWonProducts(userId);
            if (products.isEmpty()) {
                LOGGER.info(
                        "[CONTROLLER][GET][WARN] /api/profile/won-products - No won products found for user with ID: {}",
                        userId);
            }

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(
                    "[CONTROLLER][GET][ERROR] /api/profile/won-products - Error occurred for user with ID: {}: {}",
                    userId, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
