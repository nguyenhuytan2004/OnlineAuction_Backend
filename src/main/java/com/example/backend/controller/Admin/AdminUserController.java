package com.example.backend.controller.Admin;

import com.example.backend.entity.User;
import com.example.backend.model.User.CreateUserRequest;
import com.example.backend.model.User.UpdateUserAdminRequest;
import com.example.backend.model.User.UserResponse;
import com.example.backend.service.IUserService;
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
@RequestMapping("/api/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {

  private final IUserService userService;

  @Operation(summary = "Get all users (Admin)", description = "Retrieve list of all users in the system. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all users", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    try {
      return ResponseEntity.ok(
          userService.getAllUsers()
              .stream()
              .map(UserResponse::new)
              .toList());
    } catch (Exception e) {
      log.error("[ADMIN][GET_ALL][ERROR] {}", e.getMessage(), e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error occurred: " + e.getMessage());
    }
  }

  @Operation(summary = "Get user by ID (Admin)", description = "Retrieve a specific user by their ID. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable Integer id) {
    try {
      User user = userService.getUser(id);
      if (user == null) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("User not found with ID: " + id);
      }
      return ResponseEntity.ok(new UserResponse(user));
    } catch (Exception e) {
      log.error("[ADMIN][GET_BY_ID][ERROR] {}", e.getMessage(), e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error occurred: " + e.getMessage());
    }
  }

  @Operation(summary = "Create user (Admin)", description = "Create a new user account. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid user data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping
  public ResponseEntity<?> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    try {
      UserResponse user = userService.createUser(request);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(user);

    } catch (IllegalArgumentException iae) {
      log.error("[ADMIN][CREATE][ERROR] {}", iae.getMessage());
      return ResponseEntity
          .badRequest()
          .body(iae.getMessage());

    } catch (Exception e) {
      log.error("[ADMIN][CREATE][ERROR] {}", e.getMessage(), e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error occurred: " + e.getMessage());
    }
  }

  @Operation(summary = "Update user (Admin)", description = "Update user information. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/{id}")
  public ResponseEntity<?> updateUser(
      @PathVariable Integer id,
      @Valid @RequestBody UpdateUserAdminRequest request) {
    try {
      User user = userService.updateUserByAdmin(id, request);
      return ResponseEntity.ok(new UserResponse(user));

    } catch (IllegalArgumentException iae) {
      log.error("[ADMIN][UPDATE][ERROR] {}", iae.getMessage());
      return ResponseEntity
          .badRequest()
          .body(iae.getMessage());

    } catch (Exception e) {
      log.error("[ADMIN][UPDATE][ERROR] {}", e.getMessage(), e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error occurred: " + e.getMessage());
    }
  }

  // ================= DELETE =================

  @Operation(summary = "Delete user (Admin)", description = "Delete a user account. Admin endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();

    } catch (IllegalArgumentException iae) {
      log.error("[ADMIN][DELETE][ERROR] {}", iae.getMessage());
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(iae.getMessage());

    } catch (Exception e) {
      log.error("[ADMIN][DELETE][ERROR] {}", e.getMessage(), e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error occurred: " + e.getMessage());
    }
  }
}