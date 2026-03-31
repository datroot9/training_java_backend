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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentDao studentDao;
    private final StudentInfoDao studentInfoDao;
    private final JobLauncher jobLauncher;
    private final Job exportStudentsJob;
    private final StudentMapper studentMapper;

    public PageResponse<StudentResponse> getAllStudents(String code, String name, Date birthday,
            PageRequest pageRequest) {
        // Calculate offset for pagination
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int offset = (page - 1) * size;

        // Get total count
        long totalElements = studentDao.countAll(code, name, birthday);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Get paginated students from database
        List<StudentWithInfo> students = studentDao.findAllWithPaging(code, name, birthday, size, offset);

        // Map to response
        List<StudentResponse> studentResponses = students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());

        // Sort students based on provided parameters
        studentResponses = sortStudents(studentResponses, pageRequest.getSortBy(), pageRequest.getSortDirection());

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

    private List<StudentResponse> sortStudents(List<StudentResponse> students, String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return students;
        }

        Comparator<StudentResponse> comparator;

        switch (sortBy.toLowerCase()) {
            case "id":
                comparator = Comparator.comparingInt(StudentResponse::getId);
                break;
            case "name":
                comparator = Comparator.comparing(StudentResponse::getName);
                break;
            case "code":
                comparator = Comparator.comparing(StudentResponse::getCode);
                break;
            case "address":
                comparator = Comparator.comparing(StudentResponse::getAddress);
                break;
            case "averagescore":
                comparator = Comparator.comparingDouble(StudentResponse::getAverageScore);
                break;
            case "birthday":
                comparator = Comparator.comparing(StudentResponse::getBirthday);
                break;
            default:
                return students; // No sorting if field not recognized
        }

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    public StudentResponse getStudentById(int id) {
        StudentWithInfo student = studentDao.findStudentWithInfoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse getStudentByCode(String code) {
        StudentWithInfo student = studentDao.findWithInfoByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + code));
        return studentMapper.toResponse(student);
    }

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

    public Result<Student> deleteStudent(int id) {
        Student student = studentDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        Result<Student> studentResult = studentDao.delete(student);
        return studentResult;
    }

    // Removed manual toResponse methods - replaced by MapStruct mapper

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
