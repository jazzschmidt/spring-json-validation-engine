package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates JSON against all {@link RuleSet}.
 */
public class RuleSetValidator {
    private final Set<RuleSet> ruleSets = new HashSet<>();
    private final Set<Matcher<?>> matchers = new HashSet<>();
    private final Set<Validator<?>> validators = new HashSet<>();

    public RuleSetValidator(Set<Matcher<?>> matchers, Set<Validator<?>> validators) {
        this.matchers.addAll(matchers);
        this.validators.addAll(validators);
    }

    /**
     * Adds a {@link RuleSet} to the validation
     *
     * @param ruleSet rule set
     */
    public void addRuleSet(RuleSet ruleSet) {
        ruleSets.add(ruleSet);
    }

    /**
     * Removes a {@link RuleSet} from the validation
     *
     * @param ruleSet rule set
     */
    public void removeRuleSet(RuleSet ruleSet) {
        ruleSets.remove(ruleSet);
    }

    /**
     * Validates a JSON Map object.
     *
     * @param jsonMap JSON
     * @throws RuleValidationException if any validation fails
     */
    public void validate(Map<String, Object> jsonMap) throws RuleValidationException {
        JsonWrapper wrapper = new JsonWrapper(jsonMap);
        validate(wrapper);
    }

    /**
     * Validates a JSON Node object.
     *
     * @param jsonNode JSON
     * @throws RuleValidationException if any validation fails
     */
    public void validate(JsonNode jsonNode) throws RuleValidationException {
        JsonWrapper wrapper = new JsonWrapper(jsonNode);
        validate(wrapper);
    }

    /**
     * Validates JSON against all matching {@link RuleSet}s.
     *
     * @param json JSON
     * @throws RuleValidationException if any validation fails
     */
    private void validate(JsonWrapper json) throws RuleValidationException {
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
     * @throws RuleValidationException if any validation fails
     */
    private void applyRuleSet(RuleSet ruleSet, JsonWrapper json) throws RuleValidationException {
        try {
            for (Object params : ruleSet.getRules()) {
                // Retrieve all matching validators
                List<Validator<?>> validators = this.validators.stream()
                        .filter(r -> r.getTargetType() == params.getClass())
                        .collect(Collectors.toList());

                for (Validator<?> validator : validators) {
                    // Validate!
                    validator.applyObject(params, json);
                }
            }
        } catch (RuleValidationException e) {
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
        for (Object params : ruleSet.getMatchers()) {
            // Retrieve all matching matchers
            List<Matcher<?>> matchers = this.matchers.stream()
                    .filter(m -> m.getTargetType() == params.getClass())
                    .collect(Collectors.toList());

            boolean match = matchers.stream()
                    .allMatch(m -> m.matchesObject(params, json));

            if (!match) {
                // Not all matchers matched:
                return false;
            }
        }

        // Validate every JSON when no matcher is configured
        return true;
    }

}
