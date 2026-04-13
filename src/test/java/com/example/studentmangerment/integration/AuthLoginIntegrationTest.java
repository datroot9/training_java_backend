package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("Auth login integration tests")
class AuthLoginIntegrationTest extends BaseAuthIntegrationTest {

    @Test
    @DisplayName("valid credentials -> 200 and returns JWT")
    void success() throws Exception {
        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload(EXISTING_EMAIL, EXISTING_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(EXISTING_EMAIL))
                .andExpect(jsonPath("$.data.token").isString())
                .andReturn();

        String token = com.jayway.jsonpath.JsonPath.read(
                result.getResponse().getContentAsString(), "$.data.token");
        org.junit.jupiter.api.Assertions.assertTrue(jwtUtils.validateToken(token));
        org.junit.jupiter.api.Assertions.assertEquals(EXISTING_EMAIL, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("wrong password -> 401")
    void wrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload(EXISTING_EMAIL, "wrong123"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("non-existent user -> 401")
    void nonExistentUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload("no@test.com", "pass123"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("blank email -> 400 validation error")
    void blankEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload("", "pass123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.username").exists());
    }

    @Test
    @DisplayName("invalid email format -> 400 validation error")
    void invalidEmailFormat() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload("not-an-email", "pass123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.username").exists());
    }

    @Test
    @DisplayName("blank password -> 400 validation error")
    void blankPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload(EXISTING_EMAIL, ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.password").exists());
    }

    @Test
    @DisplayName("no request body -> 400")
    void noBody() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
