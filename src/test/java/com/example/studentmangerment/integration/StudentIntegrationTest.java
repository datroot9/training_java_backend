package com.example.studentmangerment.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Student integration tests (controller -> service -> dao -> H2)")
@SuppressWarnings("null")
class StudentIntegrationTest extends BaseAuthIntegrationTest {

    @AfterEach
    void cleanStudentData() {
        jdbcTemplate.update("DELETE FROM student_info");
        jdbcTemplate.update("DELETE FROM student");
    }

    @Test
    @DisplayName("create student -> 201 and can get by id/code")
    void createAndGetStudent_success() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);

        var createResult = mockMvc.perform(post("/api/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validStudentPayload("STU001"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.code").value("STU001"))
                .andReturn();

        int id = com.jayway.jsonpath.JsonPath.read(
                createResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/students/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.code").value("STU001"));

        mockMvc.perform(get("/api/students/code/{code}", "STU001")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.code").value("STU001"));
    }

    @Test
    @DisplayName("get all students with paging -> 200 and expected page data")
    void getAllStudents_success() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);
        createStudent("STU001", token);
        createStudent("STU002", token);

        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + token)
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
    @DisplayName("update existing student -> 200 and returns updated fields")
    void updateStudent_success() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);
        int id = createStudent("STU001", token);

        Map<String, Object> updatePayload = Map.of(
                "name", "Nguyen Van B",
                "code", "STU009",
                "address", "Da Nang City",
                "averageScore", 9.2,
                "birthday", "2002/12/31");

        mockMvc.perform(put("/api/students/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value("Nguyen Van B"))
                .andExpect(jsonPath("$.data.code").value("STU009"));
    }

    @Test
    @DisplayName("delete existing student -> 200 then get by id returns 404")
    void deleteStudent_success() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);
        int id = createStudent("STU001", token);

        mockMvc.perform(delete("/api/students/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/students/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("create duplicate student code -> 409")
    void createStudent_duplicateCode_returns409() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);
        createStudent("STU001", token);

        mockMvc.perform(post("/api/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validStudentPayload("STU001"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Student code already exists: STU001"));
    }

    @Test
    @DisplayName("create student with invalid request -> 400")
    void createStudent_invalidRequest_returns400() throws Exception {
        String token = jwtUtils.generateToken(EXISTING_EMAIL);
        Map<String, Object> invalidPayload = Map.of(
                "name", "",
                "code", "BAD",
                "address", "abc",
                "averageScore", 11.0,
                "birthday", "3000/01/01");

        mockMvc.perform(post("/api/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalidPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @DisplayName("student endpoints without token -> 401")
    void studentEndpoints_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    private int createStudent(String code, String token) throws Exception {
        var result = mockMvc.perform(post("/api/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validStudentPayload(code))))
                .andExpect(status().isCreated())
                .andReturn();
        return com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
    }

    private Map<String, Object> validStudentPayload(String code) {
        return Map.of(
                "name", "Nguyen Van A",
                "code", code,
                "address", "Ha Noi, Viet Nam",
                "averageScore", 8.5,
                "birthday", "2004/01/01");
    }

    private String toJson(Object value) throws Exception {
        return java.util.Objects.requireNonNull(objectMapper.writeValueAsString(value));
    }
}
