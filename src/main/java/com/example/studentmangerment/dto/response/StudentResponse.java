package com.example.studentmangerment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * API representation of a student including joined info fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    /** Student primary key. */
    private int id;
    /** Display name. */
    private String name;
    /** Business student code. */
    private String code;
    /** Address from student info. */
    private String address;
    /** Average score. */
    private double averageScore;

    /** Date of birth ({@code yyyy/MM/dd} in JSON). */
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "UTC")
    private Date birthday;
}
