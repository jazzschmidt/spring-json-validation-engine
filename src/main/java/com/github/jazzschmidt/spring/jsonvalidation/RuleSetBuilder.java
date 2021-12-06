package com.github.jazzschmidt.spring.jsonvalidation;

import com.github.jazzschmidt.spring.jsonvalidation.components.FieldEquals;
import com.github.jazzschmidt.spring.jsonvalidation.components.FieldNotEmpty;
import com.github.jazzschmidt.spring.jsonvalidation.components.FieldNotEquals;

import java.util.ArrayList;
import java.util.List;

/**
 * Incrementally builds a rule set
 */
public class RuleSetBuilder {

    private final RuleSet ruleSet = new RuleSet();
    private final List<Object> matcherDefinitions = new ArrayList<>();
    private final List<Object> ruleDefinitions = new ArrayList<>();

    public RuleSetBuilder(String name, String description) {
        ruleSet.setName(name);
        ruleSet.setDescription(description);
    }

    /**
     * Adds a match definitions
     *
     * @param matchDefinition match definition
     * @return RuleSetBuilder
     */
    public RuleSetBuilder matches(Object matchDefinition) {
        matcherDefinitions.add(matchDefinition);
        return this;
    }

    /**
     * Adds a rule definition
     *
     * @param ruleDefinition rule definition
     * @return RuleSetBuilder
     */
    public RuleSetBuilder validates(Object ruleDefinition) {
        ruleDefinitions.add(ruleDefinition);
        return this;
    }

    /**
     * Syntactic sugar, has no effect
     *
     * @return RuleSetBuilder
     */
    public RuleSetBuilder and() {
        return this;
    }

    /**
     * Syntactic sugar, has no effect
     *
     * @return RuleSetBuilder
     */
    public RuleSetBuilder when() {
        return this;
    }

    /**
     * Syntactic sugar, has no effect
     *
     * @return RuleSetBuilder
     */
    public RuleSetBuilder then() {
        return this;
    }

    /**
     * Creates the {@link RuleSet} instance
     *
     * @return configures rule set
     */
    public RuleSet build() {
        ruleSet.setMatchers(matcherDefinitions);
        ruleSet.setRules(ruleDefinitions);
        return ruleSet;
    }

    /**
     * Supplies helper methods for definitions
     */
    public static class DefinitionsSupplier {
        /**
         * Matches a field by its JSON path and the given value
         *
         * @param jsonPath JSON path
         * @param value    any value
         * @return definition for {@link Matcher} or {@link Validator}
         */
        public static FieldEquals fieldEquals(String jsonPath, Object value) {
            FieldEquals properties = new FieldEquals();
            properties.jsonPath = jsonPath;
            properties.value = value;

            return properties;
        }

        /**
         * Matches a field by its JSON path and the given value being not present
         *
         * @param jsonPath JSON path
         * @param value    any value
         * @return definition for {@link Matcher} or {@link Validator}
         */
        public static FieldNotEquals fieldNotEquals(String jsonPath, Object value) {
            FieldNotEquals properties = new FieldNotEquals();
            properties.jsonPath = jsonPath;
            properties.value = value;

            return properties;
        }

        /**
         * Matches a field by its JSON path and a non-null value
         *
         * @param jsonPath JSON path
         * @return definition for {@link Matcher} or {@link Validator}
         */
        public static FieldNotEmpty fieldNotEmpty(String jsonPath) {
            FieldNotEmpty properties = new FieldNotEmpty();
            properties.jsonPath = jsonPath;

            return properties;
        }
    }
}
