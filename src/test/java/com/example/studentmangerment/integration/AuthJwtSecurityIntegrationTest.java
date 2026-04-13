package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("Auth JWT security integration tests")
class AuthJwtSecurityIntegrationTest extends BaseAuthIntegrationTest {

    @Test
    @DisplayName("no token -> 401")
    void noToken() throws Exception {
        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid: Token is not valid or expired!"));
    }

    @Test
    @DisplayName("invalid token -> 401")
    void invalidToken() throws Exception {
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer invalid.token.here")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid: Token is not valid or expired!"));
    }

    @Test
    @DisplayName("expired token -> 401")
    void expiredToken() throws Exception {
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQHRlc3QuY29tIiwiaWF0IjoxNjAwMDAwMDAwLCJleHAiOjE2MDAwMDAwMDF9.fake")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("malformed Authorization header (no 'Bearer ' prefix) -> 401")
    void malformedHeader() throws Exception {
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "NotBearer sometoken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("valid token -> passes security filter (not 401)")
    void validToken() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);

        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(401, s,
                            "Expected to pass security filter but got 401");
                });
    }

    @Test
    @DisplayName("register -> login -> access protected endpoint with returned JWT")
    void registerLoginAccess() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterPayload(NEW_EMAIL, "pass123", "pass123"))))
                .andExpect(status().isOk());

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginPayload(NEW_EMAIL, "pass123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isString())
                .andReturn();

        String token = com.jayway.jsonpath.JsonPath.read(
                loginResult.getResponse().getContentAsString(), "$.data.token");

        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + token))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(401, s,
                            "Expected to pass security filter but got 401");
                });
    }
}
