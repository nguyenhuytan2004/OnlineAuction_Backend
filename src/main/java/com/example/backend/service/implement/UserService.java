package com.example.backend.service.implement;

import com.example.backend.entity.User;
import com.example.backend.model.User.UpdateUserRequest;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService implements IUserService {

    @Autowired
    private IUserRepository _userRepository;

    @Override
    public User getUser(Integer userId) {
        return _userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return _userRepository.findAll();
    }

    @Override
    public User updateUser(UpdateUserRequest updateUserReqeust) {

        User user = _userRepository.findById(updateUserReqeust.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + updateUserReqeust.getUserId()));

        if (!user.getEmail().equals(updateUserReqeust.getEmail())) {
            if (_userRepository.findByEmail(updateUserReqeust.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(updateUserReqeust.getEmail());
        }
        user.setFullName(updateUserReqeust.getFullName());

        return _userRepository.save(user);
    }
}
