package com.github.jazzschmidt.spring.jsonvalidation.components;

import com.github.jazzschmidt.spring.jsonvalidation.JsonWrapper;
import com.github.jazzschmidt.spring.jsonvalidation.NativeRuleSetComponent;
import com.github.jazzschmidt.spring.jsonvalidation.RuleValidationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Matches and validates the presence of a specific value at a JSON Path.
 */
@Component
public class FieldEqualsComponent extends NativeRuleSetComponent<FieldEquals> {

    public FieldEqualsComponent() {
        super(FieldEquals.class);
    }

    @Override
    public boolean matches(FieldEquals definition, JsonWrapper json) {
        Object value = readJsonPath(definition.jsonPath, json);
        return value != null && value.equals(definition.value);
    }

    @Override
    @NonNull
    protected RuleValidationException validationException(FieldEquals definition, JsonWrapper json) {
        Object value = readJsonPath(definition.jsonPath, json);
        String message = String.format("Value of %s must be `%s`, but is `%s`", definition.jsonPath, definition.value, value);

        return new RuleValidationException(message);
    }

}
