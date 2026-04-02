package com.example.studentmangerment.service;

import com.example.studentmangerment.dao.UserDao;
import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;
import com.example.studentmangerment.entity.User;
import com.example.studentmangerment.exception.AlreadyExistsException;
import com.example.studentmangerment.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest request) {
        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            throw new AlreadyExistsException("Username already exists: " + request.getUsername());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // //hash password
        request.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userDao.insert(user);

        // response for register request have no jwt
        return AuthResponse.builder()
                .username(user.getUsername())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userDao.findByUsername(request.getUsername())
                .orElse(null);
        if (user != null && bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtUtils.generateToken(user.getUsername());
            return AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .token(token)
                    .build();
        }
        throw new RuntimeException("Invalid username or password");
    }
}
