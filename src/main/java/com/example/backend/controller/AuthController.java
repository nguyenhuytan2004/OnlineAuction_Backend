package com.example.backend.controller;

import com.example.backend.model.EmailOtp.ForgotPasswordRequest;
import com.example.backend.model.EmailOtp.ResetPasswordRequest;
import com.example.backend.model.EmailOtp.VerifyEmailRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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

  /*
   * @PostMapping("/register")
   * public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
   * try {
   * authService.register(req);
   * 
   * return ResponseEntity.ok("OTP đã được gửi tới email");
   * } catch (RuntimeException e) {
   * log.warn("[CONTROLLER][AUTH][WARN] /api/auth/register - Error occurred: {}",
   * e.getMessage());
   * 
   * return new ResponseEntity<>("Email đã tồn tại", HttpStatus.CONFLICT);
   * } catch (Exception e) {
   * log.
   * error("[CONTROLLER][AUTH][ERROR] /api/auth/register - Unexpected error occurred: {}"
   * , e.getMessage(),
   * e);
   * return new ResponseEntity<>("Error occurred: " + e.getMessage(),
   * HttpStatus.INTERNAL_SERVER_ERROR);
   * }
   * }
   */

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    try {
      AuthResponse response = authService.register(req);

      return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    log.info("[CONTROLLER][AUTH][VERIFY_EMAIL][START] email={}", req.getEmail());

    try {
      AuthResponse response = authService.verifyEmail(req);

      log.info("[CONTROLLER][AUTH][VERIFY_EMAIL][SUCCESS] email={}", req.getEmail());
      return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
      log.warn("[CONTROLLER][AUTH][VERIFY_EMAIL][FAIL] email={} reason={}",
          req.getEmail(), e.getMessage());

      throw e;
    }
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

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(
      @RequestBody ForgotPasswordRequest req) {

    log.info("[CONTROLLER][AUTH][FORGOT_PASSWORD][START] email={}", req.getEmail());

    try {
      authService.forgotPassword(req);

      log.info("[CONTROLLER][AUTH][FORGOT_PASSWORD][SUCCESS] email={}", req.getEmail());
      return ResponseEntity.ok("OTP đặt lại mật khẩu đã được gửi");

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

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(
      @RequestBody ResetPasswordRequest req) {

    log.info("[CONTROLLER][AUTH][RESET_PASSWORD][START] email={}", req.getEmail());

    try {
      authService.resetPassword(req);

      log.info("[CONTROLLER][AUTH][RESET_PASSWORD][SUCCESS] email={}", req.getEmail());
      return ResponseEntity.ok("Đổi mật khẩu thành công");

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
