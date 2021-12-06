package com.github.jazzschmidt.spring.jsonvalidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an object as definition source for a @{@link Matcher}. Subjects to this annotation must be convertible via
 * Jackson serialization.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonMatcher {

    /**
     * Id of the matcher
     * @return unique id
     */
    String value();

    /**
     * Description of this matcher
     * @return descriptive text
     */
    String description() default "";

}
