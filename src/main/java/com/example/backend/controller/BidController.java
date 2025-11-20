package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.IBidService;

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
                return new ResponseEntity<>("No bids found for product ID: " + productId, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(bidder, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
