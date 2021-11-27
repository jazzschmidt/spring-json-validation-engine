package com.github.jazzschmidt.spring.jsonvalidation;

abstract public class Validator {

    abstract protected void apply() throws JsonValidationException;

}
