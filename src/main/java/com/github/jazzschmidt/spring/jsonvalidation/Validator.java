package com.github.jazzschmidt.spring.jsonvalidation;

abstract public class Validator<Definition> extends RuleSetComponent<Definition> {

    public Validator(Class<Definition> definitionType) {
        super(definitionType);
    }

    abstract protected void apply(Definition definition, JsonWrapper json) throws JsonValidationException;

    public final void applyObject(Object o, JsonWrapper json) throws JsonValidationException {
        if (!o.getClass().isAssignableFrom(getDefinitionType())) {
            throw new RuntimeException("Cannot match unsupported definition type " + getDefinitionType().getName());
        }

        apply(getDefinitionType().cast(o), json);
    }
}
