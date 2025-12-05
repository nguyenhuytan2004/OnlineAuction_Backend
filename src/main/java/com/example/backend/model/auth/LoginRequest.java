package com.example.backend.model.Auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
