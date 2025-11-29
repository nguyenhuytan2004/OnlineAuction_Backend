package com.example.backend.service.core;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.model.auth.AuthResponse;
import com.example.backend.model.auth.LoginRequest;
import com.example.backend.model.auth.RegisterRequest;
import com.example.backend.repository.IUserRepository;
import com.example.backend.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setEncryptedPassword(passwordEncoder.encode(req.getPassword())); // HASH
        user.setRole(Role.USER);

        userRepo.save(user);

        UserDetails userDetails = new CustomUserDetails(user);

        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(req.getEmail());

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }
}
