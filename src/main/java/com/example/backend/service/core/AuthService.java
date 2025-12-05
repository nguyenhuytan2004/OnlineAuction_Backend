package com.example.backend.service.core;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entity.User;
import com.example.backend.model.Auth.AuthResponse;
import com.example.backend.model.Auth.LoginRequest;
import com.example.backend.model.Auth.RegisterRequest;
import com.example.backend.model.User.UserDTO;
import com.example.backend.repository.IUserRepository;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

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
		user.setRole(User.Role.BIDDER);

		userRepo.save(user);

		UserDetails userDetails = new CustomUserDetails(user);
		UserDTO userResponse = new UserDTO(user);

		return new AuthResponse(
				jwtService.generateAccessToken(userDetails),
				jwtService.generateRefreshToken(userDetails),
				userResponse);
	}

	public AuthResponse login(LoginRequest req) {
		authManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						req.getEmail(),
						req.getPassword()));

		UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
		User user = userRepo.findByEmail(req.getEmail()).orElse(null);
		UserDTO userResponse = new UserDTO(user);

		return new AuthResponse(
				jwtService.generateAccessToken(userDetails),
				jwtService.generateRefreshToken(userDetails),
				userResponse);
	}
}
