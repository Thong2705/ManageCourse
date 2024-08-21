package com.mockproject.group3.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PageSizeValidator.class)
public @interface PageSize {
    int[] in() default { 10, 25, 50 };

    String message() default "Invalid page size, Allowed values are 10, 25, 50";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
