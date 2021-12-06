package com.github.jazzschmidt.spring.jsonvalidation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the {@link RuleValidationException} and returns an error object along with HTTP.FORBIDDEN
 */
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({RuleValidationException.class})
    public ResponseEntity<ValidationError> handleException(RuleValidationException exception) {
        return new ResponseEntity<>(new ValidationError(exception), HttpStatus.FORBIDDEN);
    }

    private static class ValidationError {
        private final RuleValidationException exception;

        public ValidationError(RuleValidationException exception) {
            this.exception = exception;
        }

        public Map<String, String> getRuleSet() {
            Map<String, String> ruleSet = new HashMap<>();
            ruleSet.put("name", exception.getRuleSet().getName());
            ruleSet.put("description", exception.getRuleSet().getDescription());

            return ruleSet;
        }

        public String getMessage() {
            return exception.getMessage();
        }
    }
}
