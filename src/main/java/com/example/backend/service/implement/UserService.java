package com.example.backend.service.implement;

import com.example.backend.entity.User;
import com.example.backend.model.User.UserDTO;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
    public User updateUser(UserDTO userDto) {
        User user = _userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(userDto.getEmail())) {
            if (_userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(userDto.getEmail());
        }

        user.setFullName(userDto.getEmail());

        return _userRepository.save(user);
    }
}
