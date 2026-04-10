package com.example.studentmangerment.controller;

import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.ApiResponse;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Date;

/**
 * REST endpoints for listing, creating, updating, and deleting students.
 *
 * <p>First sentence of a class Javadoc is the <em>summary</em> (one line) shown in IDE
 * tooltips and generated HTML indexes. Use {@code <p>} to start extra paragraphs.
 *
 * <p>Use {@link StudentService} for business logic; this class only maps HTTP to service calls.
 *
 * @see StudentService
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    /**
     * Lists students with optional filters and pagination.
     *
     * <p>Query parameters are all optional; combine filters as needed. Sorting applies when
     * {@code sortBy} is set.
     *
     * @param code          student code filter, or {@code null} to ignore
     * @param name          name filter (exact match depends on service implementation), or {@code null}
     * @param birthday      filter by date of birth, format {@code yyyy/MM/dd}, or {@code null}
     * @param page          page number (defaults to {@code 1})
     * @param size          page size (defaults to {@code 10})
     * @param sortBy        field name to sort by, or {@code null} for default order
     * @param sortDirection {@code asc} or {@code desc} (defaults to {@code asc})
     * @return HTTP 200 with a {@link ApiResponse} whose data is a {@link PageResponse} of {@link StudentResponse}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date birthday,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageResponse<StudentResponse> students = studentService.getAllStudents(code, name, birthday, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    /**
     * Returns one student by primary key.
     *
     * @param id database id from the path (e.g. {@code GET /api/students/42})
     * @return HTTP 200 and the student wrapped in {@link ApiResponse}
     * @throws com.example.studentmangerment.exception.ResourceNotFoundException
     *         if no student exists for {@code id} (handled by {@code @ControllerAdvice}, typically as 404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable int id) {
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    /** Loads a student by business {@code code} (path variable), not by numeric id. */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByCode(@PathVariable String code) {
        StudentResponse student = studentService.getStudentByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    /**
     * Creates a new student from JSON body.
     *
     * @param request input validated with Bean Validation ({@code @Valid})
     * @return HTTP 201 {@link HttpStatus#CREATED} with the created {@link StudentResponse}
     * @throws com.example.studentmangerment.exception.AlreadyExistsException
     *         if {@link StudentRequest#code} duplicates an existing student
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse student = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", student));
    }

    /**
     * Updates an existing student.
     *
     * @param id      path id of the student to update
     * @param request new field values (validated)
     * @return HTTP 200 with updated payload
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable int id,
            @Valid @RequestBody StudentRequest request) {
        StudentResponse student = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", student));
    }

    /** Deletes a student by id; response body message only (no data). */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    /**
     * Triggers batch export and streams the generated CSV as a download.
     *
     * @return {@link Resource} with {@code Content-Disposition: attachment} and {@code text/csv}
     */
    @GetMapping("/export")
    public ResponseEntity<Resource> exportStudents() {
        // Run the batch job to generate dynamic csv and get its filename
        String filename = studentService.exportStudents();

        // Load the generated CSV file
        Resource file = new FileSystemResource(filename);

        // Return the file back to the user's browser as a download attachment
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
}
