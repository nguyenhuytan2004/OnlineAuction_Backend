package com.example.backend.controller.Admin;

import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.model.SellerUpgradeRequest.ReviewSellerUpgradeRequest;
import com.example.backend.model.SellerUpgradeRequest.SellerUpgradeRequestResponse;
import com.example.backend.service.ISellerUpgradeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/seller-upgrade-requests")
@RequiredArgsConstructor
@Slf4j
public class AdminSellerUpgradeRequestController {

    private final ISellerUpgradeRequestService service;

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        try {
            return ResponseEntity.ok(
                    service.getPendingRequests()
                            .stream()
                            .map(SellerUpgradeRequestResponse::new)
                            .toList()
            );
        } catch (Exception e) {
            log.error("[ADMIN][SELLER_REQUEST][GET][ERROR]", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred");
        }
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<?> reviewRequest(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewSellerUpgradeRequest request) {

        try {
            SellerUpgradeRequest reviewed =
                    service.reviewRequest(id, request);

            return ResponseEntity.ok(
                    new SellerUpgradeRequestResponse(reviewed));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("[ADMIN][SELLER_REQUEST][REVIEW][ERROR]", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred");
        }
    }
}
