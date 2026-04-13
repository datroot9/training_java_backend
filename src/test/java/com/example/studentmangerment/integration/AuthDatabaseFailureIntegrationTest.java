package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("Auth DB failure integration tests")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AuthDatabaseFailureIntegrationTest extends BaseAuthIntegrationTest {

    @AfterEach
    void cleanupWithoutTransaction() {
        jdbcTemplate.execute("DELETE FROM user");
    }

    @Test
    @DisplayName("register when DB table is dropped -> 500")
    void registerWithDbDown() throws Exception {
        jdbcTemplate.execute("DROP TABLE user");

        try {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new RegisterPayload(NEW_EMAIL, "pass123", "pass123"))))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        } finally {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS user (user_id INT AUTO_INCREMENT PRIMARY KEY, user_name VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, role VARCHAR(32) NOT NULL DEFAULT 'USER')");
        }
    }

    @Test
    @DisplayName("login when DB table is dropped -> 500")
    void loginWithDbDown() throws Exception {
        jdbcTemplate.execute("DROP TABLE user");

        try {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new LoginPayload(EXISTING_EMAIL, EXISTING_PASSWORD))))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        } finally {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS user (user_id INT AUTO_INCREMENT PRIMARY KEY, user_name VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, role VARCHAR(32) NOT NULL DEFAULT 'USER')");
        }
    }
}
