package com.example.studentmangerment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class StudentCodeValidator implements ConstraintValidator<StudentCode, String> {

    private static final Pattern STUDENT_CODE_PATTERN = Pattern.compile("^STU\\d{3}$");

    @Override
    public void initialize(StudentCode constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return STUDENT_CODE_PATTERN.matcher(value).matches();
    }
}
