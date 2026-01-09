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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/admin/seller-upgrade-requests")
@RequiredArgsConstructor
@Slf4j
public class AdminSellerUpgradeRequestController {

  private final ISellerUpgradeRequestService service;

  @Operation(summary = "Get pending seller upgrade requests (Admin)", description = "Retrieve all pending seller upgrade requests. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved pending requests", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SellerUpgradeRequestResponse.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/pending")
  public ResponseEntity<?> getPendingRequests() {
    try {
      return ResponseEntity.ok(
          service.getPendingRequests()
              .stream()
              .map(SellerUpgradeRequestResponse::new)
              .toList());
    } catch (Exception e) {
      log.error("[ADMIN][SELLER_REQUEST][GET][ERROR]", e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error occurred");
    }
  }

  @Operation(summary = "Review seller upgrade request (Admin)", description = "Review and approve/reject a seller upgrade request. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Request reviewed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SellerUpgradeRequestResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid request data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/{id}/review")
  public ResponseEntity<?> reviewRequest(
      @PathVariable Integer id,
      @Valid @RequestBody ReviewSellerUpgradeRequest request) {

    try {
      SellerUpgradeRequest reviewed = service.reviewRequest(id, request);

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
