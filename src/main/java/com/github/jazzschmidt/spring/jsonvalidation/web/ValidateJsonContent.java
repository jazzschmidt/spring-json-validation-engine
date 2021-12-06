package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables JSON validation with all {@link RuleSet}s on the annotated handler method. This annotation has no effect, if
 * the handler method is neither one of {@code POST}, {@code PUT} or {@code PATCH}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateJsonContent {
}
