package com.example.studentmangerment.service;

import com.example.studentmangerment.dao.StudentDao;
import com.example.studentmangerment.dao.StudentInfoDao;
import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.entity.Student;
import com.example.studentmangerment.entity.StudentInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.Result;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final Map<Integer, Student> studentStorage = new HashMap<>();
    private final Map<Integer, StudentInfo> studentInfoStorage = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final StudentDao studentDao;
    private final StudentInfoDao studentInfoDao;
    @PostConstruct
    public void init() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            // Add sample students
            int id1 = idGenerator.getAndIncrement();
            Student student1 = Student.builder()
                    .id(id1)
                    .name("John Smith")
                    .code("STU001")
                    .build();
            studentStorage.put(student1.getId(), student1);

            StudentInfo info1 = StudentInfo.builder()
                    .id(id1)
                    .studentId(id1)
                    .address("123 Main Street, New York")
                    .averageScore(8.5)
                    .birthday(dateFormat.parse("2003/05/15"))
                    .build();
            studentInfoStorage.put(id1, info1);

            int id2 = idGenerator.getAndIncrement();
            Student student2 = Student.builder()
                    .id(id2)
                    .name("Jane Doe")
                    .code("STU002")
                    .build();
            studentStorage.put(student2.getId(), student2);

            StudentInfo info2 = StudentInfo.builder()
                    .id(id2)
                    .studentId(id2)
                    .address("456 Oak Avenue, Los Angeles")
                    .averageScore(9.2)
                    .birthday(dateFormat.parse("2002/08/22"))
                    .build();
            studentInfoStorage.put(id2, info2);

            int id3 = idGenerator.getAndIncrement();
            Student student3 = Student.builder()
                    .id(id3)
                    .name("Bob Wilson")
                    .code("STU003")
                    .build();
            studentStorage.put(student3.getId(), student3);

            StudentInfo info3 = StudentInfo.builder()
                    .id(id3)
                    .studentId(id3)
                    .address("789 Pine Road, Chicago")
                    .averageScore(7.8)
                    .birthday(dateFormat.parse("2003/11/10"))
                    .build();
            studentInfoStorage.put(id3, info3);
        } catch (ParseException e) {
            throw new RuntimeException("Error initializing sample data", e);
        }
    }

    public PageResponse<StudentResponse> getAllStudents(String code, String name, Date birthday, PageRequest pageRequest) {
        // Filter students based on search criteria
        List<StudentResponse> filteredStudents = studentStorage.values().stream()
                .filter(student -> code == null || student.getCode().toLowerCase().contains(code.toLowerCase()))
                .filter(student -> name == null || student.getName().toLowerCase().contains(name.toLowerCase()))
                .map(student -> {
                    StudentInfo info = studentInfoStorage.get(student.getId());
                    return toResponse(student, info);
                })
                .filter(response -> birthday == null || (response.getBirthday() != null && isSameDay(response.getBirthday(), birthday)))
                .collect(Collectors.toList());

        // Apply sorting
        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            filteredStudents = sortStudents(filteredStudents, pageRequest.getSortBy(), pageRequest.getSortDirection());
        }

        // Calculate pagination
        long totalElements = filteredStudents.size();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Get paginated data
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, filteredStudents.size());
        List<StudentResponse> paginatedData = fromIndex < filteredStudents.size()
                ? filteredStudents.subList(fromIndex, toIndex)
                : new ArrayList<>();

        // Build response
        return PageResponse.<StudentResponse>builder()
                .data(paginatedData)
                .currentPage(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    private List<StudentResponse> sortStudents(List<StudentResponse> students, String sortBy, String sortDirection) {
        Comparator<StudentResponse> comparator = null;

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
        Student student = studentDao.findById(id).orElse(null);
        if (student == null) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        StudentInfo studentInfo = studentInfoDao.findByStudentId(id).orElse(null);
        return toResponse(student, studentInfo);
    }

    public StudentResponse createStudent(StudentRequest request) {
        // Check if code already exists
        if(studentDao.findByCode(request.getCode()).isPresent()){
            throw new RuntimeException("Student code already exists");
        }

        Student student = Student.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();

        Result<Student> result =studentDao.insert(student);

        Student insertedStudent = result.getEntity();

        StudentInfo studentInfo = StudentInfo.builder()
                .studentId(insertedStudent.getId())
                .address(request.getAddress())
                .averageScore(request.getAverageScore())
                .birthday(request.getBirthday())
                .build();

        Result<StudentInfo> resultInfo = studentInfoDao.insert(studentInfo);
        StudentInfo insertedStudentInfo = resultInfo.getEntity();
        return toResponse(insertedStudent, insertedStudentInfo);
    }

    public StudentResponse updateStudent(int id, StudentRequest request) {
//        Student student = studentStorage.get(id);
        Student student = studentDao.findById(id).orElse(null);
        if (student == null) {
            throw new RuntimeException("Student not found with id: " + id);
        }

//
        StudentInfo studentInfo = studentInfoDao.findByStudentId(student.getId()).orElse(null);
        if (studentInfo == null) {
            throw new RuntimeException("Student info not found with id: " + id);
        }

        // Check if code is being changed and already exists
        String newCode = request.getCode();
        if (newCode != null && !newCode.equals(student.getCode()) && studentDao.findByCode(newCode).isPresent()) {
            throw new RuntimeException("Student code already exists");
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
        return toResponse(updatedStudent, updatedStudentInfo);
    }

    public Result<Student> deleteStudent(int id) {
        Student student = studentDao.findById(id).orElse(null);
        if (student == null) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        Result<Student> studentResult = studentDao.delete(student);
        return studentResult;
    }

    private StudentResponse toResponse(Student student, StudentInfo studentInfo) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .code(student.getCode())
                .address(studentInfo != null ? studentInfo.getAddress() : null)
                .averageScore(studentInfo != null ? studentInfo.getAverageScore() : 0.0)
                .birthday(studentInfo != null ? studentInfo.getBirthday() : null)
                .build();
    }
}
