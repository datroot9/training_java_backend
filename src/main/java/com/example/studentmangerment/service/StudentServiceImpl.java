package com.example.studentmangerment.service;

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
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.Result;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default {@link StudentService} implementation backed by Doma DAOs and Spring Batch.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    /** Student and joined-query persistence. */
    private final StudentDao studentDao;
    /** Student info row persistence. */
    private final StudentInfoDao studentInfoDao;
    /** Launches batch jobs (CSV export). */
    private final JobLauncher jobLauncher;
    /** Configured export job bean. */
    private final Job exportStudentsJob;
    /** Maps entities to API responses. */
    private final StudentMapper studentMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<StudentResponse> getAllStudents(String code, String name, Date birthday,
            PageRequest pageRequest) {
        // Calculate offset for pagination
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int offset = (page - 1) * size;

        // Get total count
        long totalElements = studentDao.countAll(code, name, birthday);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Build dynamic ORDER BY clause
        String orderByClause = validateAndGetOrderByClause(pageRequest.getSortBy(), pageRequest.getSortDirection());

        // Get paginated students from database with SQL sorting
        List<StudentWithInfo> students = studentDao.findAllWithPaging(code, name, birthday, size, offset,
                orderByClause);

        // Map to response
        List<StudentResponse> studentResponses = students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());

        // Build response
        return PageResponse.<StudentResponse>builder()
                .data(studentResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }

    /**
     * Validates supported sort fields and builds a safe SQL {@code ORDER BY} fragment.
     */
    private String validateAndGetOrderByClause(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "s.student_id ASC";
        }

        String column;
        switch (sortBy.toLowerCase()) {
            case "id":
                column = "s.student_id";
                break;
            case "name":
                column = "s.student_name";
                break;
            case "code":
                column = "s.student_code";
                break;
            case "address":
                column = "si.address";
                break;
            case "averagescore":
                column = "si.average_score";
                break;
            case "birthday":
                column = "si.date_of_birth";
                break;
            default:
                return "s.student_id ASC";
        }

        String direction = "desc".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC";
        return column + " " + direction;
    }

    // Removed manual sortStudents - sorting now happens in SQL

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentResponse getStudentById(int id) {
        StudentWithInfo student = studentDao.findStudentWithInfoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toResponse(student);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentResponse getStudentByCode(String code) {
        StudentWithInfo student = studentDao.findWithInfoByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + code));
        return studentMapper.toResponse(student);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        // Check if code already exists
        if (studentDao.findByCode(request.getCode()).isPresent()) {
            throw new AlreadyExistsException("Student code already exists: " + request.getCode());
        }

        Student student = Student.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();

        Result<Student> result = studentDao.insert(student);

        Student insertedStudent = result.getEntity();

        StudentInfo studentInfo = StudentInfo.builder()
                .studentId(insertedStudent.getId())
                .address(request.getAddress())
                .averageScore(request.getAverageScore())
                .birthday(request.getBirthday())
                .build();

        Result<StudentInfo> resultInfo = studentInfoDao.insert(studentInfo);
        StudentInfo insertedStudentInfo = resultInfo.getEntity();
        return studentMapper.toResponse(insertedStudent, insertedStudentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public StudentResponse updateStudent(int id, StudentRequest request) {
        Student student = studentDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        StudentInfo studentInfo = studentInfoDao.findByStudentId(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student info not found for student id: " + id));

        // Check if code is being changed and already exists
        String newCode = request.getCode();
        if (newCode != null && !newCode.equals(student.getCode()) && studentDao.findByCode(newCode).isPresent()) {
            throw new AlreadyExistsException("Student code already exists: " + newCode);
        }
        Student updatedStudent = Student.builder()
                .id(student.getId())
                .name(request.getName())
                .code(request.getCode())
                .build();

        StudentInfo updatedStudentInfo = StudentInfo.builder()
                .id(studentInfo.getId())
                .studentId(student.getId())
                .address(request.getAddress())
                .averageScore(request.getAverageScore())
                .birthday(request.getBirthday())
                .build();
        studentDao.update(updatedStudent);
        studentInfoDao.update(updatedStudentInfo);
        return studentMapper.toResponse(updatedStudent, updatedStudentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Result<Student> deleteStudent(int id) {
        Student student = studentDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        Result<Student> studentResult = studentDao.delete(student);
        return studentResult;
    }

    // Removed manual toResponse methods - replaced by MapStruct mapper

    /**
     * {@inheritDoc}
     */
    @Override
    public String exportStudents() {
        long timestamp = System.currentTimeMillis();
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", timestamp)
                .toJobParameters();
        try {
            jobLauncher.run(exportStudentsJob, jobParameters);
            return "student_" + timestamp + ".csv";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Export failed", e);
        }
    }
}
