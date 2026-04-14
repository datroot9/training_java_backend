package com.example.studentmangerment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

/**
 * Validator for {@link ByteSize} using UTF-8 encoding.
 */
public class ByteSizeValidator implements ConstraintValidator<ByteSize, String> {

    /** Cached max from the annotation. */
    private int max;

    @Override
    public void initialize(ByteSize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    /**
     * @return {@code true} if UTF-8 byte length ≤ max; {@code true} for {@code null}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // let @NotBlank handle null
        }
        return value.getBytes(StandardCharsets.UTF_8).length <= max;
    }
}
