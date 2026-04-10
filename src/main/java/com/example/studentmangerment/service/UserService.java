package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;

/**
 * Business contract for user authentication flows.
 */
public interface UserService {
    /**
     * Registers a new user account.
     *
     * @param request register payload
     * @return authentication response for the created user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates user credentials and returns a JWT response.
     *
     * @param request login payload
     * @return authentication response with token
     */
    AuthResponse login(LoginRequest request);
}
