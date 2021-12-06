package com.github.jazzschmidt.spring.jsonvalidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables classpath scanning for the current package or the explicitly set {@code basePackage} and searches for all
 * JSON validation definitions marked via {@link JsonMatcher} and {@link JsonRule}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonValidationComponents {
    String basePackage() default "";
}
