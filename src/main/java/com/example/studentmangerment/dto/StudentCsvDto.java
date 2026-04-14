package com.example.studentmangerment.dto;

import lombok.Builder;
import lombok.Data;

/**
 * One flattened row written to the CSV export (batch job output).
 */
@Data
@Builder
public class StudentCsvDto {
    /** Student id. */
    private int id;
    /** Student code. */
    private String code;
    /** Student name. */
    private String name;
    /** Address. */
    private String address;
    /** Average score. */
    private Double averageScore;
    /** Birthday as a pre-formatted string ({@code yyyy/MM/dd}). */
    private String birthday; // Pre-formatted date
}
