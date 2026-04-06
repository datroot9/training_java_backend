package com.example.studentmangerment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ByteSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ByteSize {
    String message() default "Value exceeds maximum byte length";

    int max();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
