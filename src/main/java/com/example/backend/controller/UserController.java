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

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

  @Autowired
  private IUserService _userService;

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
