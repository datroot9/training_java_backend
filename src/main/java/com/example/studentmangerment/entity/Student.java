package com.example.studentmangerment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seasar.doma.*;


@Entity(immutable = true)
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer id;
    @Column(name = "student_name")
    private String name;
    @Column(name = "student_code")
    private String code;

}
