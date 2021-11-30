package com.github.jazzschmidt.spring.jsonvalidation;

abstract public class Validator<Definition> {

    private final Class<Definition> definitionType;

    public Validator(Class<Definition> definitionType) {
        this.definitionType = definitionType;
    }

    abstract protected String name();

    abstract protected void apply(Definition definition, JsonWrapper json) throws JsonValidationException;

    public final void applyObject(Object o, JsonWrapper json) throws JsonValidationException {
        if (!o.getClass().isAssignableFrom(definitionType)) {
            throw new RuntimeException("Cannot match unsupported definition type " + getDefinitionType().getName());
        }

        apply(definitionType.cast(o), json);
    }

    final protected Class<Definition> getDefinitionType() {
        return definitionType;
    }
}
