package com.kramp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = MarketCodeValidator.class)
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface ValidMarketCode {

    String message() default "Unknown market code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
