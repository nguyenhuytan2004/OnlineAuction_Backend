package com.example.backend.controller;

import com.example.backend.model.EmailOtp.VerifyEmailRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Auth.AuthResponse;
import com.example.backend.model.Auth.LoginRequest;
import com.example.backend.model.Auth.RegisterRequest;
import com.example.backend.service.core.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            authService.register(req);

            return ResponseEntity.ok("OTP đã được gửi tới email");
        } catch (RuntimeException e) {
            log.warn("[CONTROLLER][AUTH][WARN] /api/auth/register - Error occurred: {}", e.getMessage());

            return new ResponseEntity<>("Email đã tồn tại", HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("[CONTROLLER][AUTH][ERROR] /api/auth/register - Unexpected error occurred: {}", e.getMessage(),
                    e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponse> verifyEmail(
            @RequestBody VerifyEmailRequest req) {
        return ResponseEntity.ok(authService.verifyEmail(req));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse response = authService.login(req);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("[CONTROLLER][AUTH][WARN] /api/auth/login - Error occurred: {}", e.getMessage());

            return new ResponseEntity<>("Tài khoản hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("[CONTROLLER][AUTH][ERROR] /api/auth/login - Unexpected error occurred: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
