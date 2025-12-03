package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.User;
import com.example.backend.model.User.UserDTO;

public interface IUserService {
    public User getUser(Integer userId);

    public List<User> getAllUsers();

    public User updateUser(UserDTO userDto);
}
