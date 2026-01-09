package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Auth.AuthResponse;
import com.example.backend.model.Auth.LoginRequest;
import com.example.backend.model.Auth.RegisterRequest;
import com.example.backend.model.EmailOtp.ForgotPasswordRequest;
import com.example.backend.model.EmailOtp.ResetPasswordRequest;
import com.example.backend.model.EmailOtp.VerifyEmailRequest;
import com.example.backend.model.EmailOtp.VerifyResetPasswordOtpRequest;
import com.example.backend.service.core.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Register a new user", description = "Create a new user account with email verification.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Registration successful, verification email sent", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    try {
      authService.register(req);

      return new ResponseEntity<>("Đăng ký thành công. Vui lòng kiểm tra email để xác minh tài khoản.",
          HttpStatus.OK);
    } catch (RuntimeException e) {
      log.warn("[CONTROLLER][AUTH][WARN] /api/auth/register - Error occurred: {}",
          e.getMessage());

      return new ResponseEntity<>("Email đã tồn tại", HttpStatus.CONFLICT);
    } catch (Exception e) {
      log.error("[CONTROLLER][AUTH][ERROR] /api/auth/register - Unexpected error occurred: {}", e.getMessage(),
          e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Resend email verification OTP", description = "Send a new OTP to the user's email for verification purposes.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OTP sent successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid email", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping("/resend-verify-email-otp")
  public ResponseEntity<?> resendVerifyEmailOtp(@RequestParam String email) {
    try {
      authService.resendVerifyEmailOtp(email);

      return new ResponseEntity<>("OTP xác minh email đã được gửi", HttpStatus.OK);
    } catch (RuntimeException e) {
      log.warn("[CONTROLLER][AUTH][WARN] /api/auth/resend-verify-email-otp - Error occurred: {}",
          e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("[CONTROLLER][AUTH][ERROR] /api/auth/resend-verify-email-otp - Unexpected error occurred: {}",
          e.getMessage(),
          e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Verify email with OTP", description = "Verify user email address using OTP sent to their email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Email verified successfully, user account created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden - invalid or expired OTP", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/verify-email")
  public ResponseEntity<?> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest req) {

    log.info("[CONTROLLER][AUTH][VERIFY_EMAIL][START] email={}", req.getEmail());

    try {
      AuthResponse response = authService.verifyEmail(req);

      log.info("[CONTROLLER][AUTH][VERIFY_EMAIL][SUCCESS] email={}", req.getEmail());
      return new ResponseEntity<>(response, HttpStatus.CREATED);

    } catch (RuntimeException e) {
      log.warn("[CONTROLLER][AUTH][VERIFY_EMAIL][FAIL] email={} reason={}",
          req.getEmail(), e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      log.error("[CONTROLLER][AUTH][VERIFY_EMAIL][ERROR] email={}",
          req.getEmail(), e);

      return new ResponseEntity<>(e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "User login", description = "Authenticate user with email and password credentials.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful, token returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "403", description = "Forbidden - account disabled", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    try {
      AuthResponse response = authService.login(req);

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (DisabledException e) {
      log.warn("[CONTROLLER][AUTH][WARN] /api/auth/login - Error occurred: {}", e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    } catch (BadCredentialsException e) {
      log.warn("[CONTROLLER][AUTH][WARN] /api/auth/login - Error occurred: {}", e.getMessage());

      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      log.error("[CONTROLLER][AUTH][ERROR] /api/auth/login - Unexpected error occurred: {}", e.getMessage(), e);
      return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Initiate password reset", description = "Send password reset OTP to user's email address.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password reset OTP sent", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid email", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(
      @RequestBody ForgotPasswordRequest req) {

    log.info("[CONTROLLER][AUTH][FORGOT_PASSWORD][START] email={}", req.getEmail());

    try {
      authService.forgotPassword(req);

      log.info("[CONTROLLER][AUTH][FORGOT_PASSWORD][SUCCESS] email={}", req.getEmail());
      return new ResponseEntity<>("OTP đặt lại mật khẩu đã được gửi", HttpStatus.OK);

    } catch (RuntimeException e) {
      log.warn("[CONTROLLER][AUTH][FORGOT_PASSWORD][FAIL] email={} reason={}",
          req.getEmail(), e.getMessage());

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());

    } catch (Exception e) {
      log.error("[CONTROLLER][AUTH][FORGOT_PASSWORD][ERROR] email={}",
          req.getEmail(), e);

      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Đã xảy ra lỗi hệ thống");
    }
  }

  @Operation(summary = "Verify password reset OTP", description = "Validate the OTP sent for password reset.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OTP verified successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid or expired OTP", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/verify-reset-password-otp")
  public ResponseEntity<?> verifyResetPasswordOtp(
      @RequestBody VerifyResetPasswordOtpRequest req) {

    log.info("[AUTH][VERIFY_RESET_OTP][START] email={}", req.getEmail());

    try {
      authService.verifyResetPasswordOtp(req);

      log.info("[AUTH][VERIFY_RESET_OTP][SUCCESS] email={}", req.getEmail());
      return ResponseEntity.ok("OTP hợp lệ");

    } catch (RuntimeException e) {
      log.warn("[AUTH][VERIFY_RESET_OTP][FAIL] email={} reason={}",
          req.getEmail(), e.getMessage());

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @Operation(summary = "Reset user password", description = "Update user password using verified OTP.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid request parameters", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(
      @RequestBody ResetPasswordRequest req) {

    log.info("[CONTROLLER][AUTH][RESET_PASSWORD][START] email={}", req.getEmail());

    try {
      authService.resetPassword(req);

      log.info("[CONTROLLER][AUTH][RESET_PASSWORD][SUCCESS] email={}", req.getEmail());
      return new ResponseEntity<>("Lấy lại mật khẩu thành công", HttpStatus.OK);

    } catch (RuntimeException e) {
      log.warn("[CONTROLLER][AUTH][RESET_PASSWORD][FAIL] email={} reason={}",
          req.getEmail(), e.getMessage());

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());

    } catch (Exception e) {
      log.error("[CONTROLLER][AUTH][RESET_PASSWORD][ERROR] email={}",
          req.getEmail(), e);

      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Đã xảy ra lỗi hệ thống");
    }
  }
}
