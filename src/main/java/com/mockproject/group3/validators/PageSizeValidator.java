package com.mockproject.group3.validators;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageSizeValidator implements ConstraintValidator<PageSize, Integer> {
    private List<Integer> allowedPageSize;

    @Override
    public void initialize(PageSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        allowedPageSize = Arrays.stream(constraintAnnotation.in()).boxed().toList();
    }

    @Override
    public boolean isValid(Integer arg0, ConstraintValidatorContext arg1) {
        return arg0 == null || allowedPageSize.contains(arg0);
    }

}
