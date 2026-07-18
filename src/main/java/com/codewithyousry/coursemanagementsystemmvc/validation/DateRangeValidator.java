package com.codewithyousry.coursemanagementsystemmvc.validation;

import com.codewithyousry.coursemanagementsystemmvc.dto.request.CourseRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, CourseRequest> {

    @Override
    public boolean isValid(CourseRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            context.buildConstraintViolationWithTemplate("End date must be after start date")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        if (request.getRegistrationStartTime() != null && request.getRegistrationEndTime() != null
                && !request.getRegistrationEndTime().isAfter(request.getRegistrationStartTime())) {
            context.buildConstraintViolationWithTemplate("Registration end must be after registration start")
                    .addPropertyNode("registrationEndTime")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
