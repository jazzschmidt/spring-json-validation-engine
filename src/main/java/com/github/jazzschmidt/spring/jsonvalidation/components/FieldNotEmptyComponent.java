package com.github.jazzschmidt.spring.jsonvalidation.components;

import com.github.jazzschmidt.spring.jsonvalidation.JsonWrapper;
import com.github.jazzschmidt.spring.jsonvalidation.NativeRuleSetComponent;
import com.github.jazzschmidt.spring.jsonvalidation.RuleValidationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Matches and validates the presence of a non-null value at a JSON Path.
 */
@Component
public class FieldNotEmptyComponent extends NativeRuleSetComponent<FieldNotEmpty> {

    public FieldNotEmptyComponent() {
        super(FieldNotEmpty.class);
    }

    @Override
    public boolean matches(FieldNotEmpty definition, JsonWrapper json) {
        return readJsonPath(definition.jsonPath, json) != null;
    }

    @Override
    @NonNull
    protected RuleValidationException validationException(FieldNotEmpty definition, JsonWrapper json) {
        return new RuleValidationException("Field must not be empty: " + definition.jsonPath);
    }
}
