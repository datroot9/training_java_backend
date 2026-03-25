package com.example.studentmangerment.entity;

import lombok.*;
import org.seasar.doma.Entity;
import org.seasar.doma.Table;


@Entity(immutable = true)
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private int id;
    private String username;
    private String password;
}
