package com.example.studentmangerment.entity;

import lombok.*;
import org.seasar.doma.*;


@Entity(immutable = true)
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "user_name")
    private String username;
    @Column(name = "password")
    private String password;
}
