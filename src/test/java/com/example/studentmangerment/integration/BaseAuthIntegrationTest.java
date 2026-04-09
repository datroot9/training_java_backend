package com.example.studentmangerment.integration;

import com.example.studentmangerment.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

abstract class BaseAuthIntegrationTest {
    protected static final String EXISTING_EMAIL = "user@test.com";
    protected static final String EXISTING_PASSWORD = "pass123";
    protected static final String NEW_EMAIL = "new@test.com";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JwtUtils jwtUtils;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(
                "INSERT INTO user (user_name, password) VALUES (?, ?)",
                EXISTING_EMAIL, passwordEncoder.encode(EXISTING_PASSWORD));
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM user");
    }

    protected record RegisterPayload(String username, String password, String confirmPassword) {
    }

    protected record LoginPayload(String username, String password) {
    }
}
