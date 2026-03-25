package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.LoginRequest;
import com.example.studentmangerment.dto.RegisterRequest;
import com.example.studentmangerment.entity.User;
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

    public User register(RegisterRequest request) {
        if (userStorage.containsKey(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
                .id((int) idGenerator.getAndIncrement())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userStorage.put(user.getUsername(), user);
        return user;
    }

    public User login(LoginRequest request) {
        User user = userStorage.get(request.getUsername());
        if (user != null) {
                return user;
        }
        throw new RuntimeException("Invalid username or password");
    }
}
