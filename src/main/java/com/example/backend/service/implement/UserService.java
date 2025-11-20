package com.example.backend.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.User;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IUserService;

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
}
