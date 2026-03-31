package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
