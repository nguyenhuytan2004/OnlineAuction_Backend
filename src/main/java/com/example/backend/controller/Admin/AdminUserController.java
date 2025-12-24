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

@RestController
@RequestMapping("/api/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {

    private final IUserService userService;


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(
                    userService.getAllUsers()
                            .stream()
                            .map(UserResponse::new)
                            .toList()
            );
        } catch (Exception e) {
            log.error("[ADMIN][GET_ALL][ERROR] {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

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


    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new UserResponse(user));

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

    // ================= UPDATE =================

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
