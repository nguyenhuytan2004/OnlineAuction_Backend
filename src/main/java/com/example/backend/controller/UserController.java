package com.example.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.model.User.CreateUserRequest;
import com.example.backend.model.User.UpdateUserRequest;
import com.example.backend.model.User.UserResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

  @Autowired
  private IUserService _userService;

  @Operation(summary = "Get all users", description = "Retrieve paginated list of users, optionally filtered by role.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("")
  public ResponseEntity<?> getUsers(@RequestParam(required = false) String role, Pageable pageable) {
    try {
      Page<User> usersPage = _userService.getUsers(role, pageable);
      Page<UserResponse> userResponses = usersPage.map(UserResponse::new);
      return new ResponseEntity<>(userResponses, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/users - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile information. Requires authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      Integer userId = userDetails.getUser().getUserId();
      User user = _userService.getUser(userId);

      if (user == null) {
        return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
      }
      UserResponse userResponse = new UserResponse(user);

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (Exception e) {
      log.error("[CONTROLLER][GET][ERROR] /api/users - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update user information", description = "Update user profile information.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid input", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("/{user_id}")
  public ResponseEntity<?> updateUser(
      @PathVariable("user_id") Integer userId,
      @Valid @RequestBody UpdateUserRequest request) {
    try {
      User updatedUser = _userService.updateUser(userId, request);
      return new ResponseEntity<>(new UserResponse(updatedUser), HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][PUT][ERROR] /api/users - Illegal argument: {}", iae.getMessage());
      return new ResponseEntity<>("Illegal argument: " + iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][PUT][ERROR] /api/users - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Delete a user", description = "Delete a user account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - cannot delete user", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/{user_id}")
  public ResponseEntity<?> deleteUser(@PathVariable("user_id") Integer userId) {
    try {
      _userService.deleteUser(userId);

      return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][DELETE][ERROR] /api/users - Illegal argument: {}", iae.getMessage());
      return new ResponseEntity<>("Illegal argument: " + iae.getMessage(), HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][DELETE][ERROR] /api/users - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Create a new user", description = "Create a new user account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - email already exists or invalid data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("")
  public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
    try {
      UserResponse newUser = _userService.createUser(createUserRequest);
      return new ResponseEntity<>(newUser, HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.error("[CONTROLLER][POST][ERROR] /api/users - Illegal argument: {}", iae.getMessage());

      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("email", iae.getMessage());
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      log.error("[CONTROLLER][POST][ERROR] /api/users - {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
