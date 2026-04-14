package com.example.studentmangerment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link AsciiOnly}; null values are considered valid (use {@code @NotNull} separately).
 */
public class AsciiOnlyValidator implements ConstraintValidator<AsciiOnly, String> {

    @Override
    public void initialize(AsciiOnly constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * @return {@code true} if every character is ASCII; {@code true} for {@code null}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull for null checks
        }

        // Check if all characters are ASCII (0-127)
        for (char c : value.toCharArray()) {
            if (c > 127) {
                return false;
            }
        }
        return true;
    }
}
