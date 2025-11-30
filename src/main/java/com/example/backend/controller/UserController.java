package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.model.user.UserDTO;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private IUserService _userService;

    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = _userService.getAllUsers();
            List<UserDTO> userDtos = users.stream()
                                        .map(UserDTO::new)
                                        .toList();
            if (userDtos.isEmpty()) {
                return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = _userService.getUser(userDetails.getUser().getUserId());
        UserDTO userDto = new UserDTO(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
