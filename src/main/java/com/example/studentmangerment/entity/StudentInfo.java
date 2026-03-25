package com.example.studentmangerment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seasar.doma.*;

import java.util.Date;
@Entity(immutable = true)
@Table(name = "student_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id")
    private int id;
    @Column(name = "student_id")
    private int studentId;
    private String address;
    @Column(name = "average_score")
    private double averageScore;
    @Column(name = "day_of_birth")
    private Date birthday;
}
