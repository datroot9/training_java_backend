package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StudentCodeValidator.class)
@Documented
public @interface StudentCode {
    String message() default "Student code must be in format STU followed by 3 digits (e.g., STU001)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
