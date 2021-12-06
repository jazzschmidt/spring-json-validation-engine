package com.github.jazzschmidt.spring.jsonvalidation;

/**
 * Matches JSON content. Derivatives of this class must provide the type of their definition in order to be accountant
 * for that {@link JsonMatcher}.
 *
 * @param <DefinitionType> class of the {@link Matcher} definition
 */
abstract public class Matcher<DefinitionType> extends RuleSetComponent {

    /**
     * Class of the definition
     */
    private final Class<DefinitionType> definitionType;

    public Matcher(Class<DefinitionType> definitionType) {
        this.definitionType = definitionType;
    }

    /**
     * Matches the JSON against this matcher with the configuration of the {@code definition} object.
     * @param definition configuration of this match
     * @param json JSON
     * @return true if the JSON matches
     */
    abstract protected boolean matches(DefinitionType definition, JsonWrapper json);

    /**
     * Matches an untyped object. Internal library method.
     *
     * @param o    object of type DefinitionType
     * @param json JSON
     * @return true if the JSON matches
     */
    public final boolean matchesObject(Object o, JsonWrapper json) {
        if (o.getClass() != definitionType) {
            throw new RuntimeException("Cannot match unsupported target type " + getTargetType().getName());
        }

        return matches((DefinitionType) o, json);
    }

    /**
     * Returns the definition type
     *
     * @return definition type
     */
    public final Class<DefinitionType> getTargetType() {
        return definitionType;
    }
}
