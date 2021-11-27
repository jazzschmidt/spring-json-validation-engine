package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.github.jazzschmidt.spring.jsonvalidation.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateJsonContent {
    Validator[] validator = new Validator[]{};
}
