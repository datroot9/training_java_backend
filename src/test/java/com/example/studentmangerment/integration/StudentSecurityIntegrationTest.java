package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("Student security integration tests")
@SuppressWarnings("null")
class StudentSecurityIntegrationTest extends BaseStudentIntegrationTest {

    @Test
    @DisplayName("without token returns 401")
    void studentEndpoints_withoutToken_returns401() throws Exception {
        mockMvc.perform(get(STUDENTS_ENDPOINT))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("invalid token returns 401")
    void studentEndpoints_invalidToken_returns401() throws Exception {
        mockMvc.perform(get(STUDENTS_ENDPOINT)
                        .header("Authorization", "Bearer invalid.token.value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid: Token is not valid or expired!"));
    }

    @Test
    @DisplayName("malformed auth header returns 401")
    void studentEndpoints_malformedHeader_returns401() throws Exception {
        mockMvc.perform(get(STUDENTS_ENDPOINT)
                        .header("Authorization", "Token abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }
}
