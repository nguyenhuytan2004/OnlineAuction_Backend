package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.IUserService;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private IUserService _userService;

    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = _userService.getAllUsers();
            if (users.isEmpty()) {
                return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public String getInfo() {
        return "USER INFO";
    }
}
