package com.github.jazzschmidt.spring.jsonvalidation;

abstract public class RuleSetComponent<Definition> {

    private final Class<Definition> definitionType;

    public RuleSetComponent(Class<Definition> definitionType) {
        this.definitionType = definitionType;
    }

    abstract protected String name();

    final protected Class<Definition> getDefinitionType() {
        return definitionType;
    }
}
