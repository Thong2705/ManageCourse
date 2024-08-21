package com.mockproject.group3.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ParamPatternValidator.class })
public @interface ParamPattern {
    String regexp() default "";

    String message() default "Invalid param accept";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
