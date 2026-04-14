package com.example.studentmangerment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seasar.doma.Entity;

import java.util.Date;

/**
 * Denormalized read model joining {@code student} and {@code student_info} for queries and batch export.
 */
@Entity(immutable = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentWithInfo {
    /** Student primary key from the {@code student} table. */
    private int id;
    /** Student display name. */
    private String name;
    /** Business student code. */
    private String code;

    /** Info row primary key from {@code student_info}, if present. */
    private Integer infoId;
    /** Address from {@code student_info}. */
    private String address;
    /** Average score from {@code student_info}. */
    private Double averageScore;
    /** Date of birth from {@code student_info}. */
    private Date birthday;

}
