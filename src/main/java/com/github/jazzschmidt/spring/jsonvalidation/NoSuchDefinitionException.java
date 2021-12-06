package com.github.jazzschmidt.spring.jsonvalidation;

/**
 * Will be thrown when an unknown definition is requested.
 */
public class NoSuchDefinitionException extends IllegalArgumentException {

    /**
     * Will be thrown when an unknown definition is requested.
     *
     * @param id id of the definition
     */
    public NoSuchDefinitionException(String id) {
        super("No rule set definition matches: " + id);
    }

}
