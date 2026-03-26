package com.example.studentmangerment.service;

import com.example.studentmangerment.dao.UserDao;
import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;
import com.example.studentmangerment.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;


    public AuthResponse register(RegisterRequest request) {
        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userDao.insert(user);

//        response for register request have no jwt
        return AuthResponse.builder()
                .username(user.getUsername())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userDao.findByUsername(request.getUsername())
                .orElse(null);
        if (user != null && user.getPassword().equals(request.getPassword())) {
            return AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .token(null) // Will be replaced with JWT token in the future
                    .build();
        }
        throw new RuntimeException("Invalid username or password");
    }
}
