package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Student validation integration tests")
@SuppressWarnings("null")
class StudentValidationIntegrationTest extends BaseStudentIntegrationTest {

    @Test
    @DisplayName("invalid payload returns 400 with validation message")
    void createStudent_invalidRequest_returns400() throws Exception {
        String authHeader = validAuthHeader();
        Map<String, Object> invalidPayload = Map.of(
                "name", "",
                "code", "BAD",
                "address", "abc",
                "averageScore", 11.0,
                "birthday", "3000/01/01");

        mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalidPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.code").exists())
                .andExpect(jsonPath("$.data.address").exists())
                .andExpect(jsonPath("$.data.averageScore").exists())
                .andExpect(jsonPath("$.data.birthday").exists());
    }

    @Test
    @DisplayName("missing request body returns 400")
    void createStudent_noBody_returns400() throws Exception {
        String authHeader = validAuthHeader();
        mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("malformed JSON returns 400")
    void createStudent_malformedJson_returns400() throws Exception {
        String authHeader = validAuthHeader();
        mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("birthday format parse error returns 400 with format message")
    void createStudent_invalidBirthdayFormat_returns400() throws Exception {
        String authHeader = validAuthHeader();
        Map<String, Object> payload = Map.of(
                "name", VALID_NAME,
                "code", "STU003",
                "address", VALID_ADDRESS,
                "averageScore", VALID_SCORE,
                "birthday", "2004-01-01");

        mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("format input birthday (yyyy/MM/dd)"));
    }
}
