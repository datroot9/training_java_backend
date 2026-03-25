package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.entity.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Integer, Student> studentStorage = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @PostConstruct
    public void init() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            // Add sample students
            Student student1 = Student.builder()
                    .id(idGenerator.getAndIncrement())
                    .name("John Smith")
                    .code("ST001")
                    .address("123 Main Street, New York")
                    .averageScore(8.5)
                    .birthday(dateFormat.parse("2003/05/15"))
                    .build();
            studentStorage.put(student1.getId(), student1);

            Student student2 = Student.builder()
                    .id(idGenerator.getAndIncrement())
                    .name("Jane Doe")
                    .code("ST002")
                    .address("456 Oak Avenue, Los Angeles")
                    .averageScore(9.2)
                    .birthday(dateFormat.parse("2002/08/22"))
                    .build();
            studentStorage.put(student2.getId(), student2);

            Student student3 = Student.builder()
                    .id(idGenerator.getAndIncrement())
                    .name("Bob Wilson")
                    .code("ST003")
                    .address("789 Pine Road, Chicago")
                    .averageScore(7.8)
                    .birthday(dateFormat.parse("2003/11/10"))
                    .build();
            studentStorage.put(student3.getId(), student3);
        } catch (ParseException e) {
            throw new RuntimeException("Error initializing sample data", e);
        }
    }

    public PageResponse<StudentResponse> getAllStudents(String code, String name, Date birthday, PageRequest pageRequest) {
        // Filter students based on search criteria
        List<StudentResponse> filteredStudents = studentStorage.values().stream()
                .filter(student -> code == null || student.getCode().toLowerCase().contains(code.toLowerCase()))
                .filter(student -> name == null || student.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(student -> birthday == null || isSameDay(student.getBirthday(), birthday))
                .map(this::toResponse)
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
        Student student = studentStorage.get(id);
        if (student == null) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        return toResponse(student);
    }

    public StudentResponse createStudent(StudentRequest request) {
        // Check if code already exists
        boolean codeExists = studentStorage.values().stream()
                .anyMatch(s -> s.getCode().equals(request.getCode()));
        if (codeExists) {
            throw new RuntimeException("Student code already exists");
        }

        Student student = Student.builder()
                .id(idGenerator.getAndIncrement())
                .name(request.getName())
                .code(request.getCode())
                .address(request.getAddress())
                .averageScore(request.getAverageScore())
                .birthday(request.getBirthday())
                .build();
        studentStorage.put(student.getId(), student);
        return toResponse(student);
    }

    public StudentResponse updateStudent(int id, StudentRequest request) {
        Student student = studentStorage.get(id);
        if (student == null) {
            throw new RuntimeException("Student not found with id: " + id);
        }

        // Check if code is being changed and already exists
        if (!student.getCode().equals(request.getCode())) {
            boolean codeExists = studentStorage.values().stream()
                    .anyMatch(s -> s.getCode().equals(request.getCode()));
            if (codeExists) {
                throw new RuntimeException("Student code already exists");
            }
        }

        student.setName(request.getName());
        student.setCode(request.getCode());
        student.setAddress(request.getAddress());
        student.setAverageScore(request.getAverageScore());
        student.setBirthday(request.getBirthday());

        studentStorage.put(id, student);
        return toResponse(student);
    }

    public void deleteStudent(int id) {
        Student student = studentStorage.remove(id);
        if (student == null) {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }

    private StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .code(student.getCode())
                .address(student.getAddress())
                .averageScore(student.getAverageScore())
                .birthday(student.getBirthday())
                .build();
    }
}
