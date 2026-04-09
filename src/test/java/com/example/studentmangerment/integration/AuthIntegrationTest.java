package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.studentmangerment.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth integration tests (controller -> service -> dao -> H2)")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("register then login returns JWT and validates it")
    void registerThenLogin_success() throws Exception {
        String username = uniqueEmail();

        try {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterPayload(username, "password123", "password123"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value(username));

            var loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginPayload(username, "password123"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value(username))
                    .andExpect(jsonPath("$.data.token").isString())
                    .andReturn();

            String token = com.jayway.jsonpath.JsonPath.read(
                    loginResult.getResponse().getContentAsString(), "$.data.token");

            org.junit.jupiter.api.Assertions.assertTrue(jwtUtils.validateToken(token));
            org.junit.jupiter.api.Assertions.assertEquals(username, jwtUtils.getUsernameFromToken(token));
        } finally {
            deleteUserByUsername(username);
        }
    }

    @Test
    @DisplayName("duplicate register returns 409 Conflict")
    void register_duplicateUsername_returns409() throws Exception {
        String username = uniqueEmail();

        try {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterPayload(username, "password123", "password123"))))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterPayload(username, "password123", "password123"))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409))
                    .andExpect(jsonPath("$.message").value("Username already exists: " + username));
        } finally {
            deleteUserByUsername(username);
        }
    }

    @Test
    @DisplayName("register with mismatched confirmPassword returns 500")
    void register_passwordMismatch_returns500() throws Exception {
        String username = uniqueEmail();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterPayload(username, "password123", "different123"))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    @DisplayName("login with wrong password returns 500")
    void login_wrongPassword_returns500() throws Exception {
        String username = uniqueEmail();

        try {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterPayload(username, "password123", "password123"))))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginPayload(username, "wrongpass"))))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        } finally {
            deleteUserByUsername(username);
        }
    }

    private void deleteUserByUsername(String username) {
        jdbcTemplate.update("DELETE FROM user WHERE user_name = ?", username);
    }

    private static String uniqueEmail() {
        String suffix = Long.toString(System.nanoTime(), 36);
        if (suffix.length() > 10) {
            suffix = suffix.substring(suffix.length() - 10);
        }
        // Keep <= 20 chars to satisfy @Size(max=20)
        return "u" + suffix + "@t.co";
    }

    private record RegisterPayload(String username, String password, String confirmPassword) {
    }

    private record LoginPayload(String username, String password) {
    }
}

