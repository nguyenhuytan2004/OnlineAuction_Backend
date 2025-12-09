package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.model.User.UpdateUserRequest;
import com.example.backend.model.User.UserResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

    @Autowired
    private IUserService _userService;

    @GetMapping("")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Integer userId = userDetails.getUser().getUserId();
            User user = _userService.getUser(userId);

            if (user == null) {
                return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(new UserResponse(user), HttpStatus.OK);

        } catch (Exception e) {
            log.error("[CONTROLLER][GET][ERROR] /api/users - {}", e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            Integer userId = userDetails.getUser().getUserId();

            if (!userId.equals(request.getUserId())) {
                return new ResponseEntity<>("Illegal argument: userId mismatch", HttpStatus.BAD_REQUEST);
            }

            User updatedUser = _userService.updateUser(request);
            return new ResponseEntity<>(new UserResponse(updatedUser), HttpStatus.OK);

        } catch (IllegalArgumentException iae) {
            log.error("[CONTROLLER][PUT][ERROR] /api/users - Illegal argument: {}", iae.getMessage());
            return new ResponseEntity<>("Illegal argument: " + iae.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("[CONTROLLER][PUT][ERROR] /api/users - {}", e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
