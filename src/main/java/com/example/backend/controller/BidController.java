package com.example.backend.controller;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/bids")
public class BidController {

    @Autowired
    private IBidService _bidService;

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
}
