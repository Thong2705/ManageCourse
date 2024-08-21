package com.mockproject.group3.validators;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ParamPatternValidator implements ConstraintValidator<ParamPattern, String[]> {

    private String regexp;

    @Override
    public void initialize(ParamPattern constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String[] arg0, ConstraintValidatorContext arg1) {
        if (arg0 == null || arg0.length == 0)
            return true;

        for (int i = 0; i < arg0.length; i++) {
            if (!Pattern.matches(regexp, arg0[i]))
                return false;
        }

        return true;
    }

}
