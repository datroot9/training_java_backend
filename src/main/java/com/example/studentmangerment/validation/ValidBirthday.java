package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Ensures a birthday is not in the future and not more than 100 years ago.
 */
@Documented
@Constraint(validatedBy = ValidBirthdayValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthday {
    /** Validation message when the constraint fails. */
    String message() default "Birthday must be in the past and within 100 years";

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Bean Validation payload types. */
    Class<? extends Payload>[] payload() default {};
}
