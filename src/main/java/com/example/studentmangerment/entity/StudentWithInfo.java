package com.example.studentmangerment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seasar.doma.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity(immutable = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentWithInfo {
    // From students table
    private int id;
    private String name;
    private String code;

    // From student_info table
    private Integer infoId;
    private String address;
    private Double averageScore;
    private Date birthday;

}
