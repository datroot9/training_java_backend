package com.example.studentmangerment.service;

import com.example.studentmangerment.dto.request.PageRequest;
import com.example.studentmangerment.dto.request.StudentRequest;
import com.example.studentmangerment.dto.response.PageResponse;
import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.entity.Student;
import org.seasar.doma.jdbc.Result;

import java.util.Date;

public interface StudentService {
    PageResponse<StudentResponse> getAllStudents(String code, String name, Date birthday, PageRequest pageRequest);
    StudentResponse getStudentById(int id);
    StudentResponse createStudent(StudentRequest request);
    StudentResponse updateStudent(int id, StudentRequest request);
    Result<Student> deleteStudent(int id);
    String exportStudents();
}
