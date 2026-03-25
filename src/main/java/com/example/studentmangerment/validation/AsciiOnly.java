package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AsciiOnlyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsciiOnly {
    String message() default "Only English letters, numbers, and basic symbols are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
