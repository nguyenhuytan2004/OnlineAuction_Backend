package com.example.backend.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RatingValueValidator.class)
public @interface RatingValue {
    String message() default "Rating value must be -1 or 1";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
