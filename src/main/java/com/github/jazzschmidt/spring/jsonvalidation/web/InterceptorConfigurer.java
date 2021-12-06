package com.github.jazzschmidt.spring.jsonvalidation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring to use the {@link ValidatorHandlerInterceptor}.
 */
@Configuration
public class InterceptorConfigurer implements WebMvcConfigurer {

    private final ValidatorHandlerInterceptor validatorHandlerInterceptor;

    @Autowired
    public InterceptorConfigurer(ValidatorHandlerInterceptor validatorHandlerInterceptor) {
        this.validatorHandlerInterceptor = validatorHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validatorHandlerInterceptor);
    }
}
