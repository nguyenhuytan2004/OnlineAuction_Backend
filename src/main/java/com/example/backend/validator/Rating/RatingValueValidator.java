package com.example.backend.validator.Rating;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RatingValueValidator implements ConstraintValidator<RatingValue, Integer> {
    @Override
    public void initialize(RatingValue constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // @NotNull sẽ handle null case
        }
        // Chỉ cho phép -1 hoặc 1
        return value == -1 || value == 1;
    }
}
