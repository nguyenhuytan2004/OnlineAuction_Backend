package com.example.backend.model.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
