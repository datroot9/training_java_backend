package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidBirthdayValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthday {
    String message() default "Birthday must be in the past and within 100 years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
