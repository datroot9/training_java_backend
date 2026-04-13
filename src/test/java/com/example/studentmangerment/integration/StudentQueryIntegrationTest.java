package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Student query integration tests")
@SuppressWarnings("null")
class StudentQueryIntegrationTest extends BaseStudentIntegrationTest {

    @Test
    @DisplayName("get all with paging")
    void getAllStudents_success() throws Exception {
        String authHeader = validAuthHeader();
        createStudent("STU001");
        createStudent("STU002");

        mockMvc.perform(get(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.data.length()").value(2));
    }

    @Test
    @DisplayName("filter by code and name")
    void getAllStudents_filterByCodeAndName() throws Exception {
        String authHeader = validAuthHeader();
        createStudent("STU001");
        createStudent("STU002");

        mockMvc.perform(get(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .param("code", "STU001")
                        .param("name", "Nguyen")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.data[0].code").value("STU001"));
    }

    @Test
    @DisplayName("invalid birthday query format returns 400")
    void getAllStudents_invalidBirthdayFormat_returns400() throws Exception {
        String authHeader = validAuthHeader();

        mockMvc.perform(get(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .param("birthday", "2004-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("The parameter 'birthday' should be of type 'Date'"));
    }

    @Test
    @DisplayName("invalid sortBy falls back to default sort")
    void getAllStudents_invalidSortBy_fallbackDefault() throws Exception {
        String authHeader = validAuthHeader();
        createStudent("STU002");
        createStudent("STU001");

        mockMvc.perform(get(STUDENTS_ENDPOINT)
                        .header("Authorization", authHeader)
                        .param("sortBy", "unknownField")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.data.length()").value(2));
    }
}
