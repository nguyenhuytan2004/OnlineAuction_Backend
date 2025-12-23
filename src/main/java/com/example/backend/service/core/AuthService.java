package com.example.backend.service.core;

import com.example.backend.entity.EmailOtp;
import com.example.backend.entity.User;
import com.example.backend.model.Auth.AuthResponse;
import com.example.backend.model.Auth.LoginRequest;
import com.example.backend.model.Auth.RegisterRequest;
import com.example.backend.model.EmailOtp.ForgotPasswordRequest;
import com.example.backend.model.EmailOtp.ResetPasswordRequest;
import com.example.backend.model.EmailOtp.VerifyEmailRequest;
import com.example.backend.model.User.UserResponse;
import com.example.backend.repository.IEmailOtpRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.security.JwtService;
import com.example.backend.service.implement.EmailOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final EmailOtpService emailOtpService;
    private final IEmailOtpRepository _emailOtpRepository;

    /*public void register(RegisterRequest req) {
        *//*if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }*//*

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setEncryptedPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.BIDDER);
        user.setIsVerified(false);

        userRepo.save(user);

        emailOtpService.sendOtp(
                user.getEmail(),
                EmailOtp.OtpType.VERIFY_EMAIL
        );
    }*/

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setEncryptedPassword(passwordEncoder.encode(req.getPassword())); // HASH
        user.setRole(User.Role.BIDDER);

        userRepo.save(user);

        UserDetails userDetails = new CustomUserDetails(user);
        com.example.backend.model.User.UserResponse userResponse = new com.example.backend.model.User.UserResponse(user);

        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails),
                userResponse);
    }

    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest req) {

        emailOtpService.validateOtp(
                req.getEmail(),
                req.getOtp(),
                EmailOtp.OtpType.VERIFY_EMAIL
        );

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsVerified(true);
        userRepo.save(user);

        _emailOtpRepository.deleteByEmailAndType(
                req.getEmail(),
                EmailOtp.OtpType.VERIFY_EMAIL
        );

        UserDetails userDetails = new CustomUserDetails(user);
        UserResponse userResponse = new UserResponse(user);

        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails),
                userResponse
        );
    }

    public AuthResponse login(LoginRequest req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsVerified()) {
            throw new RuntimeException("Email chưa được xác nhận");
        }

        UserResponse userResponse = new UserResponse(user);

        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails),
                userResponse);
    }

    public void forgotPassword(ForgotPasswordRequest req) {
        emailOtpService.sendOtp(
                req.getEmail(),
                EmailOtp.OtpType.RESET_PASSWORD
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {

        emailOtpService.validateOtp(
                req.getEmail(),
                req.getOtp(),
                EmailOtp.OtpType.RESET_PASSWORD
        );

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEncryptedPassword(
                passwordEncoder.encode(req.getNewPassword())
        );

        userRepo.save(user);

        _emailOtpRepository.deleteByEmailAndType(
                req.getEmail(),
                EmailOtp.OtpType.RESET_PASSWORD
        );
    }

}
