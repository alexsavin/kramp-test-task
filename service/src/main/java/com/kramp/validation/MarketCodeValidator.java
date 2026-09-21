package com.kramp.validation;

import com.kramp.MarketCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class MarketCodeValidator implements ConstraintValidator<ValidMarketCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // Let @NotBlank handle this
        }

        return Arrays.stream(MarketCode.values())
                .anyMatch(marketCode -> marketCode.getName().equals(value));
    }
}
