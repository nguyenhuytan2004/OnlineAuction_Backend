package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.model.user.UserDTO;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private IUserService _userService;

    /*
     * @GetMapping("")
     * public ResponseEntity<?> getAllUsers() {
     * try {
     * List<User> users = _userService.getAllUsers();
     * List<UserDTO> userDtos = users.stream()
     * .map(UserDTO::new)
     * .toList();
     * if (userDtos.isEmpty()) {
     * return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
     * }
     * return new ResponseEntity<>(userDtos, HttpStatus.OK);
     * } catch (Exception e) {
     * return new ResponseEntity<>(e.getMessage(),
     * HttpStatus.INTERNAL_SERVER_ERROR);
     * }
     * }
     */

    @GetMapping("")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = _userService.getUser(userDetails.getUser().getUserId());
        UserDTO userDto = new UserDTO(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserDTO request) {
        Integer userId = userDetails.getUser().getUserId();

        User updatedUser = _userService.updateUser(request);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
}
