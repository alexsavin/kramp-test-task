package com.kramp.controller;

import com.kramp.service.ProductCatalogUnavailableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException exception) {
        List<String> errors = exception.getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage() == null ? "Validation failed" : error.getDefaultMessage())
                .toList();

        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler(ProductCatalogUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ValidationErrorResponse handleProductCatalogUnavailableException(ProductCatalogUnavailableException exception) {
        return new ValidationErrorResponse(List.of(exception.getMessage()));
    }

    public record ValidationErrorResponse(List<String> errors) {
    }
}
