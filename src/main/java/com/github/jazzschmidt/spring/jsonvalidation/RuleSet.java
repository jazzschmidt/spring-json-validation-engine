package com.github.jazzschmidt.spring.jsonvalidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of definitions - configurations for {@link Matcher}s and {@link Validator}s) - bundled into a
 * named and described model.
 */
public class RuleSet {
    private String name, description;

    private final List<Object> matchers = new ArrayList<>();
    private final List<Object> rules = new ArrayList<>();

    /**
     * Returns the rule definitions
     *
     * @return definitions
     */
    public List<Object> getRules() {
        return new ArrayList<>(rules);
    }

    /**
     * Sets the rule definitions
     *
     * @param rules objects configuring the {@link Validator}s
     */
    public void setRules(List<Object> rules) {
        this.rules.clear();
        this.rules.addAll(rules);
    }

    /**
     * Returns the match definitions
     *
     * @return definitions
     */
    public List<Object> getMatchers() {
        return new ArrayList<>(matchers);
    }

    /**
     * Sets the match definitions
     *
     * @param matchers objects configuring the {@link Matcher}s
     */
    public void setMatchers(List<Object> matchers) {
        this.matchers.clear();
        this.matchers.addAll(matchers);
    }

    /**
     * Name of the rule set
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the rule set
     *
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Description of the rule set
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the rule set
     *
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
