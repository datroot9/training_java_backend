package com.example.studentmangerment.mapper;

import com.example.studentmangerment.dto.response.StudentResponse;
import com.example.studentmangerment.entity.Student;
import com.example.studentmangerment.entity.StudentInfo;
import com.example.studentmangerment.entity.StudentWithInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "averageScore", source = "averageScore", defaultValue = "0.0")
    StudentResponse toResponse(StudentWithInfo studentWithInfo);

    @Mapping(target = "id", source = "student.id")
    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "code", source = "student.code")
    @Mapping(target = "address", source = "info.address")
    @Mapping(target = "averageScore", source = "info.averageScore", defaultValue = "0.0")
    @Mapping(target = "birthday", source = "info.birthday")
    StudentResponse toResponse(Student student, StudentInfo info);
}
