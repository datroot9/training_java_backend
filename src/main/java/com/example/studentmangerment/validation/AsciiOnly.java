package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Ensures each character in the string is in the ASCII range (code point ≤ 127).
 */
@Documented
@Constraint(validatedBy = AsciiOnlyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsciiOnly {
    /** Validation message when the constraint fails. */
    String message() default "Only English letters, numbers, and basic symbols are allowed";

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Bean Validation payload types. */
    Class<? extends Payload>[] payload() default {};
}
