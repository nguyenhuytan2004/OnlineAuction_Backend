package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.User;

public interface IUserService {
    public User getUser(Integer userId);

    public List<User> getAllUsers();
}
