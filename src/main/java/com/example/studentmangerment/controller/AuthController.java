package com.example.studentmangerment.controller;

import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.ApiResponse;
import com.example.studentmangerment.dto.response.AuthResponse;
import com.example.studentmangerment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for user registration and login.
 *
 * <p>Maps HTTP requests to {@link UserService} and wraps results in {@link ApiResponse}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    /** Application service for register and login flows. */
    private final UserService userService;

    /**
     * Registers a new user account.
     *
     * @param request registration payload validated by Bean Validation
     * @return success response containing the registered username
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", authResponse));
    }

    /**
     * Authenticates an existing user and issues a JWT token.
     *
     * @param request login payload containing username and password
     * @return success response containing authenticated user info and token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }
}
