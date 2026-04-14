package com.example.studentmangerment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;
import java.util.Date;

/**
 * Validator for {@link ValidBirthday}; null is valid (use {@code @NotNull} separately).
 */
public class ValidBirthdayValidator implements ConstraintValidator<ValidBirthday, Date> {

    /**
     * @return {@code false} if the date is in the future or older than 100 years
     */
    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // let @NotNull handle null
        }

        Date now = new Date();

        // Birthday must not be in the future
        if (value.after(now)) {
            setMessage(context, "Birthday must not be in the future");
            return false;
        }

        // Birthday must be within 100 years
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.YEAR, -100);
        Date hundredYearsAgo = cal.getTime();

        if (value.before(hundredYearsAgo)) {
            setMessage(context, "Birthday must be within the last 100 years");
            return false;
        }

        return true;
    }

    /** Replaces the default message with a specific validation failure. */
    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
