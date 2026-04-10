package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.entity.Student;
import org.seasar.doma.jdbc.Result;

import java.util.Date;

/**
 * Business contract for student management use cases.
 */
public interface StudentService {
    /**
     * Retrieves students with optional filtering, paging, and sorting.
     *
     * @param code optional code filter
     * @param name optional name filter
     * @param birthday optional birthday filter
     * @param pageRequest pagination and sorting options
     * @return paged result of students
     */
    PageResponse<StudentResponse> getAllStudents(String code, String name, Date birthday, PageRequest pageRequest);

    /**
     * Finds one student by numeric id.
     *
     * @param id student id
     * @return matching student
     */
    StudentResponse getStudentById(int id);

    /**
     * Finds one student by business code.
     *
     * @param code student code
     * @return matching student
     */
    StudentResponse getStudentByCode(String code);

    /**
     * Creates a new student and associated student info.
     *
     * @param request create payload
     * @return created student
     */
    StudentResponse createStudent(StudentRequest request);

    /**
     * Updates an existing student and associated student info.
     *
     * @param id target student id
     * @param request update payload
     * @return updated student
     */
    StudentResponse updateStudent(int id, StudentRequest request);

    /**
     * Deletes a student by id.
     *
     * @param id student id
     * @return Doma delete result
     */
    Result<Student> deleteStudent(int id);

    /**
     * Triggers batch export and returns generated CSV filename.
     *
     * @return generated file name
     */
    String exportStudents();
}
