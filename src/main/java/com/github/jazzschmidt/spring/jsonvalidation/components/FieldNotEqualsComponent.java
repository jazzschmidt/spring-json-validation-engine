package com.github.jazzschmidt.spring.jsonvalidation.components;

import com.github.jazzschmidt.spring.jsonvalidation.JsonWrapper;
import com.github.jazzschmidt.spring.jsonvalidation.NativeRuleSetComponent;
import com.github.jazzschmidt.spring.jsonvalidation.RuleValidationException;
import org.springframework.lang.NonNull;

public class FieldNotEqualsComponent extends NativeRuleSetComponent<FieldNotEquals> {

    public FieldNotEqualsComponent() {
        super(FieldNotEquals.class);
    }

    @Override
    public boolean matches(FieldNotEquals definition, JsonWrapper json) {
        Object value = readJsonPath(definition.jsonPath, json);
        return value != null && !value.equals(definition.value);
    }

    @Override
    @NonNull
    protected RuleValidationException validationException(FieldNotEquals definition, JsonWrapper json) {
        String message = String.format("Value of %s must not be `%s`", definition.jsonPath, definition.value);

        return new RuleValidationException(message);
    }
}
