package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;
import com.github.jazzschmidt.spring.jsonvalidation.RuleSetValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Validates that handler methods annotated with {@link ValidateJsonContent} will not be invoked when the JSON in the
 * request body could not be successfully validated by means of all matching {@link RuleSet}s from the {@link
 * RuleSetValidator}.
 */
@Component
public class ValidatorHandlerInterceptor implements HandlerInterceptor {

    private final RuleSetValidator validator;
    private final ObjectMapper objectMapper;

    @Autowired
    public ValidatorHandlerInterceptor(RuleSetValidator validator, ObjectMapper objectMapper) {
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // Only validate @ValidateJsonContent handler methods
        if (shouldValidateBefore(handler)) {
            String content = JsonContentHttpRequestWrapper.getContent();
            JsonNode jsonNode = objectMapper.readTree(content);

            // Throws the RuleValidationException on any validation error
            validator.validate(jsonNode);
        }

        return true;
    }

    private boolean shouldValidateBefore(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        return ((HandlerMethod) handler).hasMethodAnnotation(ValidateJsonContent.class);
    }

}
