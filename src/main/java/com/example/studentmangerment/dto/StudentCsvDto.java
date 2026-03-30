package com.example.studentmangerment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentCsvDto {
    private int id;
    private String code;
    private String name;
    private String address;
    private Double averageScore;
    private String birthday; // Pre-formatted date
}
