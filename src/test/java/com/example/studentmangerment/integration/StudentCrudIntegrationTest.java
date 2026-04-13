package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
@DisplayName("Student CRUD integration tests")
@SuppressWarnings("null")
class StudentCrudIntegrationTest extends BaseStudentIntegrationTest {

    @Test
    @DisplayName("create -> get by id -> get by code")
    void createAndGetStudent_success() throws Exception {
        String userHeader = validAuthHeader();

        var createResult = mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validStudentPayload("STU001"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.code").value("STU001"))
                .andReturn();

        int id = com.jayway.jsonpath.JsonPath.read(
                createResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get(STUDENTS_BY_ID_ENDPOINT, id)
                        .header("Authorization", userHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.code").value("STU001"));

        mockMvc.perform(get(STUDENTS_BY_CODE_ENDPOINT, "STU001")
                        .header("Authorization", userHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.code").value("STU001"));
    }

    @Test
    @DisplayName("update existing student")
    void updateStudent_success() throws Exception {
        int id = createStudent("STU001");

        Map<String, Object> updatePayload = Map.of(
                "name", "Nguyen Van B",
                "code", "STU009",
                "address", "Da Nang City",
                "averageScore", 9.2,
                "birthday", "2002/12/31");

        mockMvc.perform(put(STUDENTS_BY_ID_ENDPOINT, id)
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value("Nguyen Van B"))
                .andExpect(jsonPath("$.data.code").value("STU009"));
    }

    @Test
    @DisplayName("delete existing student")
    void deleteStudent_success() throws Exception {
        String userHeader = validAuthHeader();
        int id = createStudent("STU001");

        mockMvc.perform(delete(STUDENTS_BY_ID_ENDPOINT, id)
                        .header("Authorization", adminAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get(STUDENTS_BY_ID_ENDPOINT, id)
                        .header("Authorization", userHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("delete as non-admin returns 403")
    void deleteStudent_asUser_returns403() throws Exception {
        String userHeader = validAuthHeader();
        int id = createStudent("STU001");

        mockMvc.perform(delete(STUDENTS_BY_ID_ENDPOINT, id)
                        .header("Authorization", userHeader))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("create as non-admin returns 403")
    void createStudent_asUser_returns403() throws Exception {
        mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", validAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validStudentPayload("STU001"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("update as non-admin returns 403")
    void updateStudent_asUser_returns403() throws Exception {
        int id = createStudent("STU001");
        Map<String, Object> updatePayload = validStudentPayload("STU009");

        mockMvc.perform(put(STUDENTS_BY_ID_ENDPOINT, id)
                        .header("Authorization", validAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePayload)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("create duplicate code returns 409")
    void createStudent_duplicateCode_returns409() throws Exception {
        createStudent("STU001");

        mockMvc.perform(post(STUDENTS_ENDPOINT)
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validStudentPayload("STU001"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Student code already exists: STU001"));
    }

    @Test
    @DisplayName("update code to existing code returns 409")
    void updateStudent_duplicateCode_returns409() throws Exception {
        int firstId = createStudent("STU001");
        int secondId = createStudent("STU002");

        Map<String, Object> updatePayload = validStudentPayload("STU001");

        mockMvc.perform(put(STUDENTS_BY_ID_ENDPOINT, secondId)
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePayload)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Student code already exists: STU001"));

        org.junit.jupiter.api.Assertions.assertTrue(firstId > 0);
    }

    @Test
    @DisplayName("get by id/code and delete non-existent student returns 404")
    void notFoundCases_returns404() throws Exception {
        String authHeader = validAuthHeader();

        mockMvc.perform(get(STUDENTS_BY_ID_ENDPOINT, 9999)
                        .header("Authorization", authHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        mockMvc.perform(get(STUDENTS_BY_CODE_ENDPOINT, "STU999")
                        .header("Authorization", authHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        mockMvc.perform(delete(STUDENTS_BY_ID_ENDPOINT, 9999)
                        .header("Authorization", adminAuthHeader()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
