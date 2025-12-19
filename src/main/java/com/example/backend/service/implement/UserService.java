package com.example.backend.service.implement;

import com.example.backend.entity.User;
import com.example.backend.model.User.CreateUserRequest;
import com.example.backend.model.User.UpdateUserAdminRequest;
import com.example.backend.model.User.UpdateUserRequest;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private IUserRepository _userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(Integer userId) {
        return _userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return _userRepository.findAll();
    }

    @Override
    public User updateUser(Integer userId, UpdateUserRequest updateUserRequest) {

        User user = _userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!user.getEmail().equals(updateUserRequest.getEmail())) {
            if (_userRepository.findByEmail(updateUserRequest.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        user.setFullName(updateUserRequest.getFullName());

        return _userRepository.save(user);
    }

    // ========= ADMIN =========

    @Override
    public User createUser(CreateUserRequest request) {

        if (_userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);

        return _userRepository.save(user);
    }

    @Override
    public User updateUserByAdmin(Integer userId, UpdateUserAdminRequest request) {

        User user = _userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEmail().equals(request.getEmail())) {
            if (_userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        user.setFullName(request.getFullName());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        return _userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = _userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        _userRepository.deleteById(userId);
    }
}
