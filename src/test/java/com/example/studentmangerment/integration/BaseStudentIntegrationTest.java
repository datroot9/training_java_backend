package com.example.studentmangerment.integration;

import java.util.Map;
import java.util.Objects;
import org.springframework.test.web.servlet.ResultActions;

abstract class BaseStudentIntegrationTest extends BaseAuthIntegrationTest {
    protected static final String STUDENTS_ENDPOINT = "/api/students";
    protected static final String STUDENTS_BY_CODE_ENDPOINT = "/api/students/code/{code}";
    protected static final String STUDENTS_BY_ID_ENDPOINT = "/api/students/{id}";

    protected static final String VALID_NAME = "Nguyen Van A";
    protected static final String VALID_ADDRESS = "Ha Noi, Viet Nam";
    protected static final double VALID_SCORE = 8.5;
    protected static final String VALID_BIRTHDAY = "2004/01/01";

    protected static final String DEFAULT_TOKEN_EMAIL = EXISTING_EMAIL;

    protected String validAuthHeader() {
        return "Bearer " + jwtUtils.generateToken(DEFAULT_TOKEN_EMAIL);
    }

    protected String adminAuthHeader() {
        return "Bearer " + jwtUtils.generateToken(ADMIN_EMAIL);
    }

    protected Map<String, Object> validStudentPayload(String code) {
        return Map.of(
                "name", VALID_NAME,
                "code", code,
                "address", VALID_ADDRESS,
                "averageScore", VALID_SCORE,
                "birthday", VALID_BIRTHDAY);
    }

    protected String toJson(Object value) throws Exception {
        return Objects.requireNonNull(objectMapper.writeValueAsString(value));
    }

    /** Creates a student using an admin token (POST is admin-only). */
    protected int createStudent(String code) throws Exception {
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(STUDENTS_ENDPOINT)
                .header("Authorization", adminAuthHeader())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(toJson(validStudentPayload(code))));

        String body = result
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return com.jayway.jsonpath.JsonPath.read(body, "$.data.id");
    }
}
