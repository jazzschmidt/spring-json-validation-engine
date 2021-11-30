package com.github.jazzschmidt.spring.jsonvalidation;

abstract public class Matcher<Definition> {

    private final Class<Definition> definitionType;

    public Matcher(Class<Definition> definitionType) {
        this.definitionType = definitionType;
    }

    /**
     * Matches the JSON against this matcher with the configuration of the {@code definition} object.
     *
     * @param definition configuration of this match
     * @param json       JSON
     * @return true if the JSON matches
     */
    abstract protected boolean matches(Definition definition, JsonWrapper json);

    /**
     * Matches an untyped object. Internal library method.
     *
     * @param o    object of type DefinitionType
     * @param json JSON
     * @return true if the JSON matches
     */
    public final boolean matchesObject(Object o, JsonWrapper json) {
        if (!o.getClass().isAssignableFrom(definitionType)) {
            throw new RuntimeException("Cannot match unsupported definition type " + getDefinitionType().getName());
        }

        return matches(definitionType.cast(o), json);
    }

    final protected Class<Definition> getDefinitionType() {
        return definitionType;
    }
}
