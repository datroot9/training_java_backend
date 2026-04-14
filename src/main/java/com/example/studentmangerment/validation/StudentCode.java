package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Student business code must match {@code STU} followed by exactly three digits (e.g. {@code STU001}).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StudentCodeValidator.class)
@Documented
public @interface StudentCode {
    /** Validation message when the constraint fails. */
    String message() default "Student code must be in format STU followed by 3 digits (e.g., STU001)";

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Bean Validation payload types. */
    Class<? extends Payload>[] payload() default {};
}
