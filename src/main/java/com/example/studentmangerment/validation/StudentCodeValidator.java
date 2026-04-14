package com.example.studentmangerment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for {@link StudentCode}.
 */
public class StudentCodeValidator implements ConstraintValidator<StudentCode, String> {

    /** Allowed pattern: STU + three digits. */
    private static final Pattern STUDENT_CODE_PATTERN = Pattern.compile("^STU\\d{3}$");

    @Override
    public void initialize(StudentCode constraintAnnotation) {
        // No initialization needed
    }

    /**
     * @return {@code false} for null or empty strings; otherwise pattern match result
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return STUDENT_CODE_PATTERN.matcher(value).matches();
    }
}
