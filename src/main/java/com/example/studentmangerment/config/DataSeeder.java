package com.example.studentmangerment.config;

import com.example.studentmangerment.dao.StudentDao;
import com.example.studentmangerment.dao.StudentInfoDao;
import com.example.studentmangerment.dao.UserDao;
import com.example.studentmangerment.entity.Student;
import com.example.studentmangerment.entity.StudentInfo;
import com.example.studentmangerment.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final StudentInfoDao studentInfoDao;

    @Override
    @Transactional
    public void run(String... args) {
        User user = userDao.findByUsername("admin@gmail.com").orElseGet(
                () -> {
                    User newUser = User.builder()
                            .username("admin@gmail.com")
                            .password("password123")
                            .build();
                    userDao.insert(newUser);
                    return newUser;
                }
        );

        Student student = studentDao.findByCode("STU001")
                .orElseGet(() -> {
                    Student newStudent = Student.builder()
                            .name("Huynh Dat")
                            .code("STU001")
                            .build();
                    studentDao.insert(newStudent);
                    return newStudent;
                });

        if (studentInfoDao.findByStudentId(student.getId()).isEmpty()) {
            StudentInfo studentInfo = StudentInfo.builder()
                    .studentId(student.getId())
                    .address("Tokyo")
                    .averageScore(85.5)
                    .birthday(new Date())
                    .build();
            studentInfoDao.insert(studentInfo);
        }
    }

}
