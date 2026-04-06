package com.example.studentmangerment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.seasar.doma.jdbc.Result;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;

import com.example.studentmangerment.dao.StudentDao;
import com.example.studentmangerment.dao.StudentInfoDao;
import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.entity.Student;
import com.example.studentmangerment.entity.StudentInfo;
import com.example.studentmangerment.entity.StudentWithInfo;
import com.example.studentmangerment.exception.AlreadyExistsException;
import com.example.studentmangerment.exception.ResourceNotFoundException;
import com.example.studentmangerment.mapper.StudentMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for StudentServiceImpl")
class StudentServiceTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private StudentInfoDao studentInfoDao;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job exportStudentsJob;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    // Shared test data
    private StudentRequest studentRequest;
    private Student student;
    private StudentInfo studentInfo;
    private StudentWithInfo studentWithInfo;
    private StudentResponse studentResponse;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        Date birthday = new Date();

        studentRequest = StudentRequest.builder()
                .name("Nguyen Van A")
                .code("SV001")
                .address("Ha Noi")
                .averageScore(8.5)
                .birthday(birthday)
                .build();

        student = Student.builder()
                .id(1)
                .name("Nguyen Van A")
                .code("SV001")
                .build();

        studentInfo = StudentInfo.builder()
                .id(1)
                .studentId(1)
                .address("Ha Noi")
                .averageScore(8.5)
                .birthday(birthday)
                .build();

        studentWithInfo = StudentWithInfo.builder()
                .id(1)
                .name("Nguyen Van A")
                .code("SV001")
                .infoId(1)
                .address("Ha Noi")
                .averageScore(8.5)
                .birthday(birthday)
                .build();

        studentResponse = StudentResponse.builder()
                .id(1)
                .name("Nguyen Van A")
                .code("SV001")
                .address("Ha Noi")
                .averageScore(8.5)
                .birthday(birthday)
                .build();

        pageRequest = PageRequest.builder()
                .page(1)
                .size(10)
                .sortBy(null)
                .sortDirection("asc")
                .build();
    }

    // ==========================================
    // GET ALL STUDENTS (with pagination)
    // ==========================================
    @Nested
    @DisplayName("Tests for getAllStudents()")
    class GetAllStudentsTests {

        @Test
        @DisplayName("Should return paginated students successfully")
        void testGetAllStudents_Success() {
            // Arrange
            when(studentDao.countAll(isNull(), isNull(), isNull())).thenReturn(1L);
            when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0), anyString()))
                    .thenReturn(List.of(studentWithInfo));
            when(studentMapper.toResponse(any(StudentWithInfo.class))).thenReturn(studentResponse);

            // Act
            PageResponse<StudentResponse> result = studentService.getAllStudents(null, null, null, pageRequest);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getData().size());
            assertEquals(1, result.getCurrentPage());
            assertEquals(10, result.getPageSize());
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertFalse(result.isHasNext());
            assertFalse(result.isHasPrevious());
            verify(studentDao, times(1)).countAll(isNull(), isNull(), isNull());
        }

        @Test
        @DisplayName("Should return empty page when no students found")
        void testGetAllStudents_Empty() {
            // Arrange
            when(studentDao.countAll(isNull(), isNull(), isNull())).thenReturn(0L);
            when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0), anyString()))
                    .thenReturn(List.of());

            // Act
            PageResponse<StudentResponse> result = studentService.getAllStudents(null, null, null, pageRequest);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getData().size());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("Should calculate hasNext and hasPrevious correctly for middle page")
        void testGetAllStudents_MiddlePage() {
            // Arrange
            PageRequest middlePageRequest = PageRequest.builder()
                    .page(2).size(5).sortBy(null).sortDirection("asc").build();

            when(studentDao.countAll(isNull(), isNull(), isNull())).thenReturn(15L); // 3 pages total
            when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(5), eq(5), anyString()))
                    .thenReturn(List.of(studentWithInfo));
            when(studentMapper.toResponse(any(StudentWithInfo.class))).thenReturn(studentResponse);

            // Act
            PageResponse<StudentResponse> result = studentService.getAllStudents(null, null, null, middlePageRequest);

            // Assert
            assertEquals(2, result.getCurrentPage());
            assertEquals(3, result.getTotalPages());
            assertTrue(result.isHasNext()); // page 2 < 3 total pages
            assertTrue(result.isHasPrevious()); // page 2 > 1
        }

        @Test
        @DisplayName("Should use sort by name descending when specified")
        void testGetAllStudents_SortByName() {
            // Arrange
            PageRequest sortRequest = PageRequest.builder()
                    .page(1).size(10).sortBy("name").sortDirection("desc").build();

            when(studentDao.countAll(isNull(), isNull(), isNull())).thenReturn(1L);
            when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0),
                    eq("s.student_name DESC")))
                    .thenReturn(List.of(studentWithInfo));
            when(studentMapper.toResponse(any(StudentWithInfo.class))).thenReturn(studentResponse);

            // Act
            PageResponse<StudentResponse> result = studentService.getAllStudents(null, null, null, sortRequest);

            // Assert
            assertNotNull(result);
            // Verify the correct ORDER BY clause was passed to DAO
            verify(studentDao).findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0),
                    eq("s.student_name DESC"));
        }

        @Test
        @DisplayName("Should default to student_id ASC when sortBy is invalid")
        void testGetAllStudents_InvalidSortBy() {
            // Arrange
            PageRequest sortRequest = PageRequest.builder()
                    .page(1).size(10).sortBy("invalid_field").sortDirection("asc").build();

            when(studentDao.countAll(isNull(), isNull(), isNull())).thenReturn(1L);
            when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0),
                    eq("s.student_id ASC")))
                    .thenReturn(List.of(studentWithInfo));
            when(studentMapper.toResponse(any(StudentWithInfo.class))).thenReturn(studentResponse);

            // Act
            studentService.getAllStudents(null, null, null, sortRequest);

            // Assert — defaults to student_id ASC
            verify(studentDao).findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0),
                    eq("s.student_id ASC"));
        }
    }

    // ==========================================
    // GET STUDENT BY ID
    // ==========================================
    @Nested
    @DisplayName("Tests for getStudentById()")
    class GetStudentByIdTests {

        @Test
        @DisplayName("Should return student when found by id")
        void testGetStudentById_Success() {
            // Arrange
            when(studentDao.findStudentWithInfoById(1)).thenReturn(Optional.of(studentWithInfo));
            when(studentMapper.toResponse(any(StudentWithInfo.class))).thenReturn(studentResponse);

            // Act
            StudentResponse result = studentService.getStudentById(1);

            // Assert
            assertNotNull(result);
            assertEquals("Nguyen Van A", result.getName());
            assertEquals("SV001", result.getCode());
            verify(studentDao, times(1)).findStudentWithInfoById(1);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when student not found by id")
        void testGetStudentById_NotFound() {
            // Arrange
            when(studentDao.findStudentWithInfoById(999)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> studentService.getStudentById(999));
            assertEquals("Student not found with id: 999", ex.getMessage());
        }
    }

    // ==========================================
    // GET STUDENT BY CODE
    // ==========================================
    @Nested
    @DisplayName("Tests for getStudentByCode()")
    class GetStudentByCodeTests {

        @Test
        @DisplayName("Should return student when found by code")
        void testGetStudentByCode_Success() {
            // Arrange
            when(studentDao.findWithInfoByCode("SV001")).thenReturn(Optional.of(studentWithInfo));
            when(studentMapper.toResponse(any(StudentWithInfo.class))).thenReturn(studentResponse);

            // Act
            StudentResponse result = studentService.getStudentByCode("SV001");

            // Assert
            assertNotNull(result);
            assertEquals("SV001", result.getCode());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when student not found by code")
        void testGetStudentByCode_NotFound() {
            // Arrange
            when(studentDao.findWithInfoByCode("INVALID")).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> studentService.getStudentByCode("INVALID"));
            assertEquals("Student not found with code: INVALID", ex.getMessage());
        }
    }

    // ==========================================
    // CREATE STUDENT
    // ==========================================
    @Nested
    @DisplayName("Tests for createStudent()")
    class CreateStudentTests {

        @Test
        @DisplayName("Should create student successfully when code is unique")
        @SuppressWarnings("unchecked")
        void testCreateStudent_Success() {
            // Arrange
            when(studentDao.findByCode("SV001")).thenReturn(Optional.empty());

            Result<Student> mockStudentResult = (Result<Student>) org.mockito.Mockito.mock(Result.class);
            when(mockStudentResult.getEntity()).thenReturn(student);
            when(studentDao.insert(any(Student.class))).thenReturn(mockStudentResult);

            Result<StudentInfo> mockInfoResult = (Result<StudentInfo>) org.mockito.Mockito.mock(Result.class);
            when(mockInfoResult.getEntity()).thenReturn(studentInfo);
            when(studentInfoDao.insert(any(StudentInfo.class))).thenReturn(mockInfoResult);

            when(studentMapper.toResponse(any(Student.class), any(StudentInfo.class))).thenReturn(studentResponse);

            // Act
            StudentResponse result = studentService.createStudent(studentRequest);

            // Assert
            assertNotNull(result);
            assertEquals("Nguyen Van A", result.getName());
            assertEquals("SV001", result.getCode());
            verify(studentDao, times(1)).insert(any(Student.class));
            verify(studentInfoDao, times(1)).insert(any(StudentInfo.class));
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException when student code already exists")
        void testCreateStudent_CodeAlreadyExists() {
            // Arrange
            when(studentDao.findByCode("SV001")).thenReturn(Optional.of(student));

            // Act & Assert
            AlreadyExistsException ex = assertThrows(AlreadyExistsException.class,
                    () -> studentService.createStudent(studentRequest));
            assertEquals("Student code already exists: SV001", ex.getMessage());

            // Verify: no insert should happen
            verify(studentDao, never()).insert(any(Student.class));
            verify(studentInfoDao, never()).insert(any(StudentInfo.class));
        }
    }

    // ==========================================
    // UPDATE STUDENT
    // ==========================================
    @Nested
    @DisplayName("Tests for updateStudent()")
    class UpdateStudentTests {

        @Test
        @DisplayName("Should update student successfully")
        void testUpdateStudent_Success() {
            // Arrange
            when(studentDao.findById(1)).thenReturn(Optional.of(student));
            when(studentInfoDao.findByStudentId(1)).thenReturn(Optional.of(studentInfo));
            when(studentMapper.toResponse(any(Student.class), any(StudentInfo.class))).thenReturn(studentResponse);

            // Act
            StudentResponse result = studentService.updateStudent(1, studentRequest);

            // Assert
            assertNotNull(result);
            verify(studentDao, times(1)).update(any(Student.class));
            verify(studentInfoDao, times(1)).update(any(StudentInfo.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when student id not found")
        void testUpdateStudent_StudentNotFound() {
            // Arrange
            when(studentDao.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> studentService.updateStudent(999, studentRequest));
            assertEquals("Student not found with id: 999", ex.getMessage());

            verify(studentDao, never()).update(any(Student.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when student info not found")
        void testUpdateStudent_StudentInfoNotFound() {
            // Arrange
            when(studentDao.findById(1)).thenReturn(Optional.of(student));
            when(studentInfoDao.findByStudentId(1)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> studentService.updateStudent(1, studentRequest));
            assertEquals("Student info not found for student id: 1", ex.getMessage());

            verify(studentDao, never()).update(any(Student.class));
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException when changing to an existing code")
        void testUpdateStudent_DuplicateCode() {
            // Arrange — student has code "SV001", request tries to change to "SV002"
            StudentRequest changeCodeRequest = StudentRequest.builder()
                    .name("Nguyen Van A").code("SV002").address("Ha Noi")
                    .averageScore(8.5).birthday(new Date()).build();

            Student existingOtherStudent = Student.builder().id(2).name("Other").code("SV002").build();

            when(studentDao.findById(1)).thenReturn(Optional.of(student));
            when(studentInfoDao.findByStudentId(1)).thenReturn(Optional.of(studentInfo));
            when(studentDao.findByCode("SV002")).thenReturn(Optional.of(existingOtherStudent));

            // Act & Assert
            AlreadyExistsException ex = assertThrows(AlreadyExistsException.class,
                    () -> studentService.updateStudent(1, changeCodeRequest));
            assertEquals("Student code already exists: SV002", ex.getMessage());

            verify(studentDao, never()).update(any(Student.class));
        }

        @Test
        @DisplayName("Should allow update when code is not changed")
        void testUpdateStudent_SameCode() {
            // Arrange — same code "SV001" as existing student
            when(studentDao.findById(1)).thenReturn(Optional.of(student));
            when(studentInfoDao.findByStudentId(1)).thenReturn(Optional.of(studentInfo));
            // findByCode is NOT called because code hasn't changed
            when(studentMapper.toResponse(any(Student.class), any(StudentInfo.class))).thenReturn(studentResponse);

            // Act
            StudentResponse result = studentService.updateStudent(1, studentRequest);

            // Assert — update should proceed without duplicate check
            assertNotNull(result);
            verify(studentDao, times(1)).update(any(Student.class));
            verify(studentDao, never()).findByCode(anyString()); // code didn't change, no need to check
        }
    }

    // ==========================================
    // DELETE STUDENT
    // ==========================================
    @Nested
    @DisplayName("Tests for deleteStudent()")
    class DeleteStudentTests {

        @Test
        @DisplayName("Should delete student successfully")
        @SuppressWarnings("unchecked")
        void testDeleteStudent_Success() {
            // Arrange
            when(studentDao.findById(1)).thenReturn(Optional.of(student));
            Result<Student> mockResult = (Result<Student>) org.mockito.Mockito.mock(Result.class);
            when(studentDao.delete(any(Student.class))).thenReturn(mockResult);

            // Act
            Result<Student> result = studentService.deleteStudent(1);

            // Assert
            assertNotNull(result);
            verify(studentDao, times(1)).findById(1);
            verify(studentDao, times(1)).delete(student);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent student")
        void testDeleteStudent_NotFound() {
            // Arrange
            when(studentDao.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> studentService.deleteStudent(999));
            assertEquals("Student not found with id: 999", ex.getMessage());

            verify(studentDao, never()).delete(any(Student.class));
        }
    }

}
