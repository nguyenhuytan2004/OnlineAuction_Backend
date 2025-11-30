package com.example.backend.service;

import com.example.backend.entity.User;

import java.util.List;

public interface IUserService {
    public User getUser(Integer userId);

    public List<User> getAllUsers();
}
