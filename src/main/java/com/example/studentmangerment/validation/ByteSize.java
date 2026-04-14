package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Limits the UTF-8 byte length of a string (not necessarily the same as {@link String#length()}).
 */
@Documented
@Constraint(validatedBy = ByteSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ByteSize {
    /** Validation message when the constraint fails. */
    String message() default "Value exceeds maximum byte length";

    /** Maximum allowed UTF-8 byte length. */
    int max();

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Bean Validation payload types. */
    Class<? extends Payload>[] payload() default {};
}
