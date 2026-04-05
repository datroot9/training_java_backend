package com.example.studentmangerment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.studentmangerment.dao.UserDao;
import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;
import com.example.studentmangerment.entity.User;
import com.example.studentmangerment.exception.AlreadyExistsException;
import com.example.studentmangerment.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserServiceImpl")
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("test@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("test@gmail.com");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id(1)
                .username("test@gmail.com")
                .password("hashedPassword")
                .build();
    }

    @Nested
    @DisplayName("Tests for register()")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void testRegister_Success() {
            // Arrange
            when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
            when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPassword");

            // Act
            AuthResponse response = userService.register(registerRequest);

            // Assert
            assertNotNull(response);
            assertEquals(registerRequest.getUsername(), response.getUsername());
            verify(userDao, times(1)).insert(any(User.class));
            verify(bCryptPasswordEncoder, times(1)).encode("password123");
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException when username already exists")
        void testRegister_UsernameExists() {
            // Arrange
            when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));

            // Act & Assert
            assertThrows(AlreadyExistsException.class, () -> userService.register(registerRequest));
            verify(userDao, never()).insert(any(User.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when passwords do not match")
        void testRegister_PasswordsDoNotMatch() {
            // Arrange
            registerRequest.setConfirmPassword("differentPassword");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> userService.register(registerRequest));
            verify(userDao, never()).insert(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for login()")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login and return JWT token")
        void testLogin_Success() {
            // Arrange
            when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtUtils.generateToken(anyString())).thenReturn("mock-jwt-token");

            // Act
            AuthResponse response = userService.login(loginRequest);

            // Assert
            assertNotNull(response);
            assertEquals("mock-jwt-token", response.getToken());
            assertEquals(user.getUsername(), response.getUsername());
            verify(jwtUtils, times(1)).generateToken(user.getUsername());
        }

        @Test
        @DisplayName("Should throw RuntimeException for non-existent user")
        void testLogin_UserNotFound() {
            // Arrange
            when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
            verify(jwtUtils, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("Should throw RuntimeException for incorrect password")
        void testLogin_InvalidPassword() {
            // Arrange
            when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
            verify(jwtUtils, never()).generateToken(anyString());
        }
    }
}
