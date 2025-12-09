package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.model.User.UpdateUserRequest;

import java.util.List;

public interface IUserService {
    public User getUser(Integer userId);

    public List<User> getAllUsers();

    public User updateUser(UpdateUserRequest updateUserResponse);
}
