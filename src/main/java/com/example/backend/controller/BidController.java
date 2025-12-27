package com.example.backend.controller;

import com.example.backend.entity.Bid;
import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.entity.User;
import com.example.backend.model.Bid.CreateBidRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IBidService;
import com.example.backend.service.ISellerUpgradeRequestService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/bids")
public class BidController {

    @Autowired
    private IBidService _bidService;

    @Autowired
    private ISellerUpgradeRequestService sellerUpgradeRequestService;


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

    @PostMapping("/seller-upgrade-request")
    public ResponseEntity<?> createSellerUpgradeRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            Integer userId = userDetails.getUser().getUserId();
            SellerUpgradeRequest request =
                    sellerUpgradeRequestService.createRequest(userId);

            return new ResponseEntity<>(request, HttpStatus.CREATED);

        } catch (IllegalStateException ise) {
            log.warn("[SELLER_UPGRADE][WARN] {}", ise.getMessage());
            return new ResponseEntity<>(ise.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (IllegalArgumentException iae) {
            log.warn("[SELLER_UPGRADE][WARN] {}", iae.getMessage());
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("[SELLER_UPGRADE][ERROR]", e);
            return new ResponseEntity<>(
                    "Failed to create seller upgrade request",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/seller-upgrade-request/status")
    public ResponseEntity<?> getSellerUpgradeRequestStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer userId = userDetails.getUser().getUserId();

        return sellerUpgradeRequestService
                .getLatestRequestByUser(userId)
                .map(req -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("hasRequest", true);
                    body.put("status", req.getStatus());
                    body.put("createdAt", req.getRequestAt());
                    body.put("reviewedAt", req.getReviewedAt());
                    body.put("comments", req.getComments());
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> ResponseEntity.ok(
                        Map.of(
                                "hasRequest", false,
                                "status", "NONE"
                        )
                ));
    }

}
