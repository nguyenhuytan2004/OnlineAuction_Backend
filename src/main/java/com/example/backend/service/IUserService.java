package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.entity.User;
import com.example.backend.model.User.CreateUserRequest;
import com.example.backend.model.User.UpdateUserRequest;
import com.example.backend.model.User.UserResponse;

public interface IUserService {
  public Page<User> getUsers(String role, Pageable pageable);

  public User getUser(Integer userId);

  public List<User> getAllUsers();

  public User updateUser(Integer userId, UpdateUserRequest updateUserResponse);

  UserResponse createUser(CreateUserRequest request);

  void deleteUser(Integer userId);
}
