package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;
import com.example.studentmangerment.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Map<String, User> userStorage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        // Add sample users
        User user1 = User.builder()
                .id((int) idGenerator.getAndIncrement())
                .username("admin@gmail.com")
                .password("admin123")
                .build();
        userStorage.put(user1.getUsername(), user1);

        User user2 = User.builder()
                .id((int) idGenerator.getAndIncrement())
                .username("john_doe@gmail.com")
                .password("password123")
                .build();
        userStorage.put(user2.getUsername(), user2);

        User user3 = User.builder()
                .id((int) idGenerator.getAndIncrement())
                .username("jane_smith@gmail.com")
                .password("secure456")
                .build();
        userStorage.put(user3.getUsername(), user3);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userStorage.containsKey(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        User user = User.builder()
                .id((int) idGenerator.getAndIncrement())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userStorage.put(user.getUsername(), user);

//        response for register request have no jwt
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userStorage.get(request.getUsername());
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
