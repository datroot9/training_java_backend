package com.example.studentmangerment.integration;

import com.example.studentmangerment.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it-mysql")
@Transactional
abstract class BaseAuthIntegrationTest {
    protected static final String EXISTING_EMAIL = "user@test.com";
    protected static final String EXISTING_PASSWORD = "pass123";
    protected static final String NEW_EMAIL = "new@test.com";
    protected static final String ADMIN_EMAIL = "admin@test.com";

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
        String encoded = passwordEncoder.encode(EXISTING_PASSWORD);
        jdbcTemplate.update(
                "INSERT INTO user (user_name, password, role) VALUES (?, ?, ?)",
                EXISTING_EMAIL, encoded, "USER");
        jdbcTemplate.update(
                "INSERT INTO user (user_name, password, role) VALUES (?, ?, ?)",
                ADMIN_EMAIL, encoded, "ADMIN");
    }

    protected record RegisterPayload(String username, String password, String confirmPassword) {
    }

    protected record LoginPayload(String username, String password) {
    }
}
