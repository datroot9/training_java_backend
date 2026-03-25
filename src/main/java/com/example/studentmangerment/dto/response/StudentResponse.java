package com.example.studentmangerment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private int id;
    private String name;
    private String code;
    private String address;
    private double averageScore;

    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "UTC")
    private Date birthday;
}
