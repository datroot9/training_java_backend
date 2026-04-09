package com.example.studentmangerment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.studentmangerment.config.SecurityConfig;
import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.exception.AlreadyExistsException;
import com.example.studentmangerment.exception.ResourceNotFoundException;
import com.example.studentmangerment.security.JwtAuthenticationFilter;
import com.example.studentmangerment.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = StudentController.class, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class) })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Unit tests for StudentController")
public class StudentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private StudentService studentService;

        @Autowired
        private ObjectMapper objectMapper;

        private StudentResponse studentResponse;
        private StudentRequest validRequest;

        @BeforeEach
        void setUp() {
                studentResponse = StudentResponse.builder()
                                .id(1)
                                .name("Nguyen Van A")
                                .code("STU001")
                                .address("Ha Noi, Viet Nam")
                                .averageScore(8.5)
                                .birthday(new Date())
                                .build();

                validRequest = StudentRequest.builder()
                                .name("Nguyen Van A")
                                .code("STU001")
                                .address("Ha Noi, Viet Nam")
                                .averageScore(8.5)
                                .birthday(new Date())
                                .build();
        }

        // ==========================================
        // GET /api/students
        // ==========================================
        @Nested
        @DisplayName("Tests for GET /api/students")
        class GetAllStudentsEndpoint {

                @Test
                @DisplayName("Should return 200 OK with paginated students")
                void testGetAllStudents_Success() throws Exception {
                        // Arrange
                        PageResponse<StudentResponse> pageResponse = PageResponse.<StudentResponse>builder()
                                        .data(List.of(studentResponse))
                                        .currentPage(1)
                                        .pageSize(10)
                                        .totalElements(1)
                                        .totalPages(1)
                                        .hasNext(false)
                                        .hasPrevious(false)
                                        .build();

                        Mockito.when(studentService.getAllStudents(isNull(), isNull(), isNull(),
                                        any(PageRequest.class)))
                                        .thenReturn(pageResponse);

                        // Act & Assert
                        mockMvc.perform(get("/api/students"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.data[0].name").value("Nguyen Van A"))
                                        .andExpect(jsonPath("$.data.totalElements").value(1));
                }

                @Test
                @DisplayName("Should pass search parameters to service")
                void testGetAllStudents_WithSearchParams() throws Exception {
                        // Arrange
                        PageResponse<StudentResponse> pageResponse = PageResponse.<StudentResponse>builder()
                                        .data(List.of())
                                        .currentPage(1).pageSize(5).totalElements(0).totalPages(0)
                                        .hasNext(false).hasPrevious(false).build();

                        Mockito.when(studentService.getAllStudents(anyString(), anyString(), any(),
                                        any(PageRequest.class)))
                                        .thenReturn(pageResponse);

                        // Act & Assert
                        mockMvc.perform(get("/api/students")
                                        .param("code", "STU001")
                                        .param("name", "Nguyen")
                                        .param("page", "2")
                                        .param("size", "5"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200));
                }
        }

        // ==========================================
        // GET /api/students/{id}
        // ==========================================
        @Nested
        @DisplayName("Tests for GET /api/students/{id}")
        class GetStudentByIdEndpoint {

                @Test
                @DisplayName("Should return 200 OK when student found by id")
                void testGetStudentById_Success() throws Exception {
                        // Arrange
                        Mockito.when(studentService.getStudentById(1)).thenReturn(studentResponse);

                        // Act & Assert
                        mockMvc.perform(get("/api/students/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.name").value("Nguyen Van A"))
                                        .andExpect(jsonPath("$.data.code").value("STU001"));
                }

                @Test
                @DisplayName("Should return 404 Not Found when student does not exist")
                void testGetStudentById_NotFound() throws Exception {
                        // Arrange
                        Mockito.when(studentService.getStudentById(999))
                                        .thenThrow(new ResourceNotFoundException("Student not found with id: 999"));

                        // Act & Assert
                        mockMvc.perform(get("/api/students/999"))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.code").value(404))
                                        .andExpect(jsonPath("$.message").value("Student not found with id: 999"));
                }
        }

        // ==========================================
        // GET /api/students/code/{code}
        // ==========================================
        @Nested
        @DisplayName("Tests for GET /api/students/code/{code}")
        class GetStudentByCodeEndpoint {

                @Test
                @DisplayName("Should return 200 OK when student found by code")
                void testGetStudentByCode_Success() throws Exception {
                        // Arrange
                        Mockito.when(studentService.getStudentByCode("STU001")).thenReturn(studentResponse);

                        // Act & Assert
                        mockMvc.perform(get("/api/students/code/STU001"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.code").value("STU001"));
                }

                @Test
                @DisplayName("Should return 404 when student code not found")
                void testGetStudentByCode_NotFound() throws Exception {
                        // Arrange
                        Mockito.when(studentService.getStudentByCode("INVALID"))
                                        .thenThrow(new ResourceNotFoundException(
                                                        "Student not found with code: INVALID"));

                        // Act & Assert
                        mockMvc.perform(get("/api/students/code/INVALID"))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.message").value("Student not found with code: INVALID"));
                }
        }

        // ==========================================
        // POST /api/students
        // ==========================================
        @Nested
        @DisplayName("Tests for POST /api/students")
        class CreateStudentEndpoint {

                @Test
                @DisplayName("Should return 201 Created when student is created successfully")
                void testCreateStudent_Success() throws Exception {
                        // Arrange
                        Mockito.when(studentService.createStudent(any(StudentRequest.class)))
                                        .thenReturn(studentResponse);

                        // Act & Assert
                        mockMvc.perform(post("/api/students")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validRequest)))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.name").value("Nguyen Van A"))
                                        .andExpect(jsonPath("$.data.code").value("STU001"));

                        verify(studentService, times(1)).createStudent(any(StudentRequest.class));
                }

                @Test
                @DisplayName("Should return 409 Conflict when student code already exists")
                void testCreateStudent_DuplicateCode() throws Exception {
                        // Arrange
                        Mockito.when(studentService.createStudent(any(StudentRequest.class)))
                                        .thenThrow(new AlreadyExistsException("Student code already exists: STU001"));

                        // Act & Assert
                        mockMvc.perform(post("/api/students")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validRequest)))
                                        .andExpect(status().isConflict())
                                        .andExpect(jsonPath("$.code").value(409))
                                        .andExpect(jsonPath("$.message").value("Student code already exists: STU001"));
                }

                @ParameterizedTest(name = "[{index}] {4}")
                @MethodSource("createValidationCases")
                @DisplayName("Should return 400 Bad Request for invalid input")
                void testCreateStudent_ValidationFailure(String name, String code, String address,
                                Double averageScore, String description, String expectedField, String expectedError)
                                throws Exception {
                        // Arrange
                        StudentRequest request = StudentRequest.builder()
                                        .name(name)
                                        .code(code)
                                        .address(address)
                                        .averageScore(averageScore)
                                        .birthday(new Date())
                                        .build();

                        // Act & Assert
                        mockMvc.perform(post("/api/students")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value("Validation failed"))
                                        .andExpect(jsonPath("$.data." + expectedField).value(expectedError));

                        verify(studentService, never()).createStudent(any(StudentRequest.class));
                }

                static Stream<Arguments> createValidationCases() {
                        return Stream.of(
                                        // name validation
                                        Arguments.of(null, "STU001", "Ha Noi, Viet Nam", 8.5,
                                                        "Empty name", "name", "Name is required"),
                                        Arguments.of("A", "STU001", "Ha Noi, Viet Nam", 8.5,
                                                        "Name too short", "name",
                                                        "Name must be between 2 and 100 characters"),
                                        // code validation
                                        Arguments.of("Nguyen Van A", "INVALID", "Ha Noi, Viet Nam", 8.5,
                                                        "Invalid code format", "code",
                                                        "Student code must be in format STU followed by 3 digits (e.g., STU001)"),
                                        // address validation
                                        Arguments.of("Nguyen Van A", "STU001", null, 8.5,
                                                        "Empty address", "address", "Address is required"),
                                        Arguments.of("Nguyen Van A", "STU001", "HN", 8.5,
                                                        "Address too short", "address",
                                                        "Address must not exceed 255 bytes"),
                                        // averageScore validation
                                        Arguments.of("Nguyen Van A", "STU001", "Ha Noi, Viet Nam", -1.0,
                                                        "Score below 0", "averageScore",
                                                        "Average score must be at least 0.0"),
                                        Arguments.of("Nguyen Van A", "STU001", "Ha Noi, Viet Nam", 11.0,
                                                        "Score above 10", "averageScore",
                                                        "Average score must be at most 10.0"),
                                        // byte-size validation
                                        Arguments.of("あいうえおかき", "STU001", "Ha Noi, Viet Nam", 8.5,
                                                        "Name exceeds 20 bytes", "name",
                                                        "Name must not exceed 20 bytes"),
                                        Arguments.of("Nguyen Van A", "STU001",
                                                        "あ".repeat(86), 8.5,
                                                        "Address exceeds 255 bytes", "address",
                                                        "Address must not exceed 255 bytes"));
                }

                @Test
                @DisplayName("Should return 400 when birthday is in the future")
                void testCreateStudent_FutureBirthday() throws Exception {
                        // Arrange
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, 1);

                        StudentRequest request = StudentRequest.builder()
                                        .name("Nguyen Van A").code("STU001")
                                        .address("Ha Noi, Viet Nam").averageScore(8.5)
                                        .birthday(cal.getTime()).build();

                        // Act & Assert
                        mockMvc.perform(post("/api/students")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.data.birthday").value("Birthday must not be in the future"));

                        verify(studentService, never()).createStudent(any(StudentRequest.class));
                }

                @Test
                @DisplayName("Should return 400 when birthday is more than 100 years ago")
                void testCreateStudent_TooOldBirthday() throws Exception {
                        // Arrange
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, -101);

                        StudentRequest request = StudentRequest.builder()
                                        .name("Nguyen Van A").code("STU001")
                                        .address("Ha Noi, Viet Nam").averageScore(8.5)
                                        .birthday(cal.getTime()).build();

                        // Act & Assert
                        mockMvc.perform(post("/api/students")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.data.birthday").value("Birthday must be within the last 100 years"));

                        verify(studentService, never()).createStudent(any(StudentRequest.class));
                }
        }
        // ==========================================
        @Nested
        @DisplayName("Tests for PUT /api/students/{id}")
        class UpdateStudentEndpoint {

                @Test
                @DisplayName("Should return 200 OK when student is updated successfully")
                void testUpdateStudent_Success() throws Exception {
                        // Arrange
                        Mockito.when(studentService.updateStudent(anyInt(), any(StudentRequest.class)))
                                        .thenReturn(studentResponse);

                        // Act & Assert
                        mockMvc.perform(put("/api/students/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.data.name").value("Nguyen Van A"));

                        verify(studentService, times(1)).updateStudent(anyInt(), any(StudentRequest.class));
                }

                @Test
                @DisplayName("Should return 404 Not Found when updating non-existent student")
                void testUpdateStudent_NotFound() throws Exception {
                        // Arrange
                        Mockito.when(studentService.updateStudent(anyInt(), any(StudentRequest.class)))
                                        .thenThrow(new ResourceNotFoundException("Student not found with id: 999"));

                        // Act & Assert
                        mockMvc.perform(put("/api/students/999")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validRequest)))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.code").value(404))
                                        .andExpect(jsonPath("$.message").value("Student not found with id: 999"));
                }

                @ParameterizedTest(name = "[{index}] {4}")
                @MethodSource("updateValidationCases")
                @DisplayName("Should return 400 Bad Request for invalid update input")
                void testUpdateStudent_ValidationFailure(String name, String code, String address,
                                Double averageScore, String description, String expectedField, String expectedError)
                                throws Exception {
                        // Arrange
                        StudentRequest request = StudentRequest.builder()
                                        .name(name)
                                        .code(code)
                                        .address(address)
                                        .averageScore(averageScore)
                                        .birthday(new Date())
                                        .build();

                        // Act & Assert
                        mockMvc.perform(put("/api/students/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value("Validation failed"))
                                        .andExpect(jsonPath("$.data." + expectedField).value(expectedError));

                        verify(studentService, times(0)).updateStudent(anyInt(), any(StudentRequest.class));
                }

                static Stream<Arguments> updateValidationCases() {
                        return Stream.of(
                                        // name validation
                                        Arguments.of(null, "STU001", "Ha Noi, Viet Nam", 8.5,
                                                        "Empty name", "name", "Name is required"),
                                        Arguments.of("A", "STU001", "Ha Noi, Viet Nam", 8.5,
                                                        "Name too short", "name",
                                                        "Name must be between 2 and 100 characters"),
                                        // code validation
                                        Arguments.of("Nguyen Van A", "INVALID", "Ha Noi, Viet Nam", 8.5,
                                                        "Invalid code format", "code",
                                                        "Student code must be in format STU followed by 3 digits (e.g., STU001)"),
                                        // address validation
                                        Arguments.of("Nguyen Van A", "STU001", null, 8.5,
                                                        "Empty address", "address", "Address is required"),
                                        Arguments.of("Nguyen Van A", "STU001", "HN", 8.5,
                                                        "Address too short", "address",
                                                        "Address must not exceed 255 bytes"),
                                        // averageScore validation
                                        Arguments.of("Nguyen Van A", "STU001", "Ha Noi, Viet Nam", -1.0,
                                                        "Score below 0", "averageScore",
                                                        "Average score must be at least 0.0"),
                                        Arguments.of("Nguyen Van A", "STU001", "Ha Noi, Viet Nam", 11.0,
                                                        "Score above 10", "averageScore",
                                                        "Average score must be at most 10.0"),
                                        // byte-size validation
                                        Arguments.of("あいうえおかき", "STU001", "Ha Noi, Viet Nam", 8.5,
                                                        "Name exceeds 20 bytes", "name",
                                                        "Name must not exceed 20 bytes"),
                                        Arguments.of("Nguyen Van A", "STU001", "a".repeat(256), 8.5,
                                                        "Address exceeds 255 bytes", "address",
                                                        "Address must not exceed 255 bytes"));
                }

                @Test
                @DisplayName("Should return 400 when birthday is in the future")
                void testUpdateStudent_FutureBirthday() throws Exception {
                        // Arrange
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, 1);

                        StudentRequest request = StudentRequest.builder()
                                        .name("Nguyen Van A").code("STU001")
                                        .address("Ha Noi, Viet Nam").averageScore(8.5)
                                        .birthday(cal.getTime()).build();

                        // Act & Assert
                        mockMvc.perform(put("/api/students/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.data.birthday").value("Birthday must not be in the future"));

                        verify(studentService, never()).updateStudent(anyInt(), any(StudentRequest.class));
                }

                @Test
                @DisplayName("Should return 400 when birthday is more than 100 years ago")
                void testUpdateStudent_TooOldBirthday() throws Exception {
                        // Arrange
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, -101);

                        StudentRequest request = StudentRequest.builder()
                                        .name("Nguyen Van A").code("STU001")
                                        .address("Ha Noi, Viet Nam").averageScore(8.5)
                                        .birthday(cal.getTime()).build();

                        // Act & Assert
                        mockMvc.perform(put("/api/students/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.data.birthday").value("Birthday must be within the last 100 years"));

                        verify(studentService, never()).updateStudent(anyInt(), any(StudentRequest.class));
                }
        }
        // ==========================================
        @Nested
        @DisplayName("Tests for DELETE /api/students/{id}")
        class DeleteStudentEndpoint {

                @Test
                @DisplayName("Should return 200 OK when student is deleted successfully")
                void testDeleteStudent_Success() throws Exception {
                        // Act & Assert
                        mockMvc.perform(delete("/api/students/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.code").value(200))
                                        .andExpect(jsonPath("$.message").value("Student deleted successfully"));

                        verify(studentService, times(1)).deleteStudent(1);
                }

                @Test
                @DisplayName("Should return 404 Not Found when deleting non-existent student")
                void testDeleteStudent_NotFound() throws Exception {
                        // Arrange
                        Mockito.doThrow(new ResourceNotFoundException("Student not found with id: 999"))
                                        .when(studentService).deleteStudent(999);

                        // Act & Assert
                        mockMvc.perform(delete("/api/students/999"))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.code").value(404))
                                        .andExpect(jsonPath("$.message").value("Student not found with id: 999"));
                }
        }
}
