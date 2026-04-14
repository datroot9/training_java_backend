package com.example.studentmangerment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Management Spring Boot application.
 */
@SpringBootApplication
public class StudentMangermentApplication {

    /**
     * Bootstraps the application and starts the embedded web server.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentMangermentApplication.class, args);
    }

}
