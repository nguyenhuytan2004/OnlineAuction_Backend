package com.example.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Bid;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.service.IBidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/bids")
public class BidController {

    @Autowired
    private IBidService _bidService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BidController.class);

    @GetMapping("/product/{product_id}/highest-bidder")
    public ResponseEntity<?> getHighestBidderByProductId(@PathVariable("product_id") Integer productId) {
        try {
            User bidder = _bidService.getHighestBidderByProductId(productId);

            if (bidder == null) {
                LOGGER.warn(
                        "[CONTROLLER][GET][WARN] /api/bids/product/{}/highest-bidder - No bids found for product ID: {}",
                        productId, productId);
                return new ResponseEntity<>("No bids found for product ID: " + productId, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(bidder, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("[CONTROLLER][GET][ERROR] /api/bids/product/{}/highest-bidder - An error occurred: {}",
                    productId, e.getMessage());
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<?> placeBid(@Valid @RequestBody CreateBidRequest createBidRequest) {
        try {
            Bid newBid = _bidService.placeBid(createBidRequest);
            return new ResponseEntity<>(newBid, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
