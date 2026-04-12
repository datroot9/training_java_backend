package com.example.studentmangerment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.studentmangerment.config.SecurityConfig;
import com.example.studentmangerment.dto.request.LoginRequest;
import com.example.studentmangerment.dto.request.RegisterRequest;
import com.example.studentmangerment.dto.response.AuthResponse;
import com.example.studentmangerment.exception.AlreadyExistsException;
import com.example.studentmangerment.security.JwtAuthenticationFilter;
import com.example.studentmangerment.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

@WebMvcTest(controllers = AuthController.class, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class) })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Unit tests for AuthController")
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @Nested
        @DisplayName("Tests for /api/auth/register")
        class RegisterEndpoint {

                @Test
                @DisplayName("Should return 200 OK when registration is successful")
                void testRegister_Success() throws Exception {
                        // Arrange
                        RegisterRequest request = new RegisterRequest();
                        request.setUsername("test@gmail.com");
                        request.setPassword("password123");
                        request.setConfirmPassword("password123");

                        AuthResponse response = AuthResponse.builder()
                                        .username("test@gmail.com")
                                        .build();

                        Mockito.when(userService.register(any(RegisterRequest.class))).thenReturn(response);

                        // Act & Assert
                        mockMvc.perform(post("/api/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.username").value("test@gmail.com"));

                        verify(userService, times(1)).register(any(RegisterRequest.class));
                }

                @Test
                @DisplayName("Should return 409 Conflict when user already exists")
                void testRegister_AlreadyExists() throws Exception {
                        // Arrange
                        RegisterRequest request = new RegisterRequest();
                        request.setUsername("exists@gmail.com");
                        request.setPassword("password123");
                        request.setConfirmPassword("password123");

                        Mockito.when(userService.register(any(RegisterRequest.class)))
                                        .thenThrow(new AlreadyExistsException("User already exists"));

                        // Act & Assert
                        mockMvc.perform(post("/api/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isConflict())
                                        .andExpect(jsonPath("$.code").value(409))
                                        .andExpect(jsonPath("$.message").value("User already exists"));

                        verify(userService, times(1)).register(any(RegisterRequest.class));
                }

                @ParameterizedTest(name = "[{index}] {3}")
                @MethodSource("registerValidationCases")
                @DisplayName("Should return 400 Bad Request with specific field error for invalid register input")
                void testRegister_ValidationFailure(String username, String password, String confirmPassword,
                                String description, String expectedField, String expectedFieldError)
                                throws Exception {
                        // Arrange
                        RegisterRequest request = new RegisterRequest();
                        request.setUsername(username);
                        request.setPassword(password);
                        request.setConfirmPassword(confirmPassword);

                        // Act & Assert
                        mockMvc.perform(post("/api/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message", containsString(expectedFieldError)))
                                        .andExpect(jsonPath("$.data." + expectedField).value(expectedFieldError));

                        verify(userService, times(0)).register(any(RegisterRequest.class));
                }

                static Stream<Arguments> registerValidationCases() {
                        return Stream.of(
                                        Arguments.of("", "password123", "password123",
                                                        "Empty email", "username", "Email is required"),
                                        Arguments.of("invalid-email", "password123", "password123",
                                                        "Invalid email format", "username", "Invalid email format"),
                                        Arguments.of("verylongemail12345@gmail.com", "password123", "password123",
                                                        "Email too long (>20)", "username",
                                                        "Email must be less than 20 characters"),
                                        Arguments.of("test@gmail.com", "short", "short",
                                                        "Password too short", "password",
                                                        "Password must be at least 6 characters and no more than 15 characters"),
                                        Arguments.of("test@gmail.com", "thispasswordiswaytoolong", "thispasswordiswaytoolong",
                                                        "Password too long (>15)", "password",
                                                        "Password must be at least 6 characters and no more than 15 characters"),
                                        // @AsciiOnly validation
                                        Arguments.of("テスト@gmail.com", "password123", "password123",
                                                        "Non-ASCII username", "username",
                                                        "Username can only contain English letters, numbers, and basic symbols"),
                                        Arguments.of("test@gmail.com", "パスワード123456", "パスワード123456",
                                                        "Non-ASCII password", "password",
                                                        "Password can only contain English letters, numbers, and basic symbols"));
                }
        }

        @Nested
        @DisplayName("Tests for /api/auth/login")
        class LoginEndpoint {

                @Test
                @DisplayName("Should return 200 OK when login is successful")
                void testLogin_Success() throws Exception {
                        // Arrange
                        LoginRequest request = new LoginRequest();
                        request.setUsername("test@gmail.com");
                        request.setPassword("password123");

                        AuthResponse response = AuthResponse.builder()
                                        .username("test@gmail.com")
                                        .token("mock-token")
                                        .build();

                        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(response);

                        // Act & Assert
                        mockMvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.token").value("mock-token"));

                        verify(userService, times(1)).login(any(LoginRequest.class));
                }

                @Test
                @DisplayName("Should return 401 Unauthorized for invalid credentials")
                void testLogin_InvalidCredentials() throws Exception {
                        // Arrange
                        LoginRequest request = new LoginRequest();
                        request.setUsername("wrong@gmail.com");
                        request.setPassword("wrongpass");

                        Mockito.when(userService.login(any(LoginRequest.class)))
                                        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                        "Invalid email or password"));

                        // Act & Assert
                        mockMvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isUnauthorized())
                                        .andExpect(jsonPath("$.message").value("Invalid email or password"));

                        verify(userService, times(1)).login(any(LoginRequest.class));
                }

                @ParameterizedTest(name = "[{index}] {2}")
                @MethodSource("loginValidationCases")
                @DisplayName("Should return 400 Bad Request with specific field error for invalid login input")
                void testLogin_ValidationFailure(String username, String password,
                                String description, String expectedField, String expectedFieldError)
                                throws Exception {
                        // Arrange
                        LoginRequest request = new LoginRequest();
                        request.setUsername(username);
                        request.setPassword(password);

                        // Act & Assert
                        mockMvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message", containsString(expectedFieldError)))
                                        .andExpect(jsonPath("$.data." + expectedField).value(expectedFieldError));

                        verify(userService, times(0)).login(any(LoginRequest.class));
                }

                static Stream<Arguments> loginValidationCases() {
                        return Stream.of(
                                        Arguments.of("", "password123",
                                                        "Empty email", "username", "Email is required"),
                                        Arguments.of("invalid-email", "password123",
                                                        "Invalid email format", "username", "Invalid email format"),
                                        Arguments.of("verylongemail12345@gmail.com", "password123",
                                                        "Email too long (>20)", "username",
                                                        "Email must be less than 20 characters"),
                                        Arguments.of("test@gmail.com", "short",
                                                        "Password too short", "password",
                                                        "Password must be at least 6 characters and no more than 15 characters"),
                                        Arguments.of("test@gmail.com", "thispasswordiswaytoolong",
                                                        "Password too long (>15)", "password",
                                                        "Password must be at least 6 characters and no more than 15 characters"),
                                        // @AsciiOnly validation
                                        Arguments.of("テスト@gmail.com", "password123",
                                                        "Non-ASCII username", "username",
                                                        "Username can only contain English letters, numbers, and basic symbols"),
                                        Arguments.of("test@gmail.com", "パスワード123456",
                                                        "Non-ASCII password", "password",
                                                        "Password can only contain English letters, numbers, and basic symbols"));
                }
        }
}
