package com.github.jazzschmidt.spring.jsonvalidation;

abstract public class Validator<DefinitionType> extends RuleSetComponent {
    private final Class<DefinitionType> targetType;

    protected Validator(Class<DefinitionType> targetType) {
        this.targetType = targetType;
    }

    abstract protected void apply(DefinitionType definition, JsonWrapper json) throws RuleValidationException;

    public final void applyObject(Object o, JsonWrapper json) throws RuleValidationException {
        if (o.getClass() != getTargetType()) {
            throw new RuntimeException("Cannot match unsupported target type " + getTargetType().getName());
        }

        apply((DefinitionType) o, json);
    }

    public final Class<DefinitionType> getTargetType() {
        return targetType;
    }
}
