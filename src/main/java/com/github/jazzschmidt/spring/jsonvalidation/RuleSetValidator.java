package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleSetValidator {
    private final Set<RuleSet> ruleSets = new HashSet<>();
    private final Set<Matcher<?>> matchers = new HashSet<>();
    private final Set<Validator<?>> validators = new HashSet<>();

    private final JsonWrapperFactory jsonWrapperFactory;

    public RuleSetValidator(Set<Matcher<?>> matchers, Set<Validator<?>> validators, JsonWrapperFactory jsonWrapperFactory) {
        this.matchers.addAll(matchers);
        this.validators.addAll(validators);
        this.jsonWrapperFactory = jsonWrapperFactory;
    }

    public void addRuleSet(RuleSet ruleSet) {
        ruleSets.add(ruleSet);
    }

    public void validate(Map<String, Object> json) throws JsonValidationException {
        validate(jsonWrapperFactory.wrap(json));
    }

    public void validate(JsonNode json) throws JsonValidationException {
        validate(jsonWrapperFactory.wrap(json));
    }

    public void validate(JsonWrapper json) throws JsonValidationException {
        for (RuleSet ruleSet : ruleSets) {
            if (ruleSetMatches(ruleSet, json)) {
                applyRuleSet(ruleSet, json);
            }
        }
    }

    /**
     * Applies a {@link RuleSet} to the given JSON.
     *
     * @param ruleSet rule set
     * @param json    JSON
     * @throws JsonValidationException if any validation fails
     */
    private void applyRuleSet(RuleSet ruleSet, JsonWrapper json) throws JsonValidationException {
        try {
            for (Object definition : ruleSet.getValidators().entrySet()) {
                // Retrieve all matching validators
                List<Validator<?>> validators = filterComponents(definition.getClass(), this.validators);

                for (Validator<?> validator : validators) {
                    // Validate!
                    validator.applyObject(definition, json);
                }
            }
        } catch (JsonValidationException e) {
            e.setRuleSet(ruleSet);
            throw e;
        }
    }

    /**
     * Determines if a {@link RuleSet} matches the given JSON.
     *
     * @param ruleSet rule set
     * @param json    JSON
     * @return true if the JSON should be validated
     */
    private boolean ruleSetMatches(RuleSet ruleSet, JsonWrapper json) {
        for (Object definition : ruleSet.getMatchers().entrySet()) {
            // Retrieve all matching matchers
            List<Matcher<?>> matchers = filterComponents(definition.getClass(), this.matchers);

            boolean match = matchers.stream()
                    .allMatch(m -> m.matchesObject(definition, json));

            if (!match) {
                // Not all matchers matched:
                return false;
            }
        }

        // Validate every JSON when no matcher is configured
        return true;
    }

    private <Component extends RuleSetComponent<?>> List<Component> filterComponents(Class<?> definitionType,
                                                                                     Set<Component> components) {
        return components.stream()
                .filter(component -> component.getDefinitionType().isAssignableFrom(definitionType))
                .collect(Collectors.toList());
    }
}
