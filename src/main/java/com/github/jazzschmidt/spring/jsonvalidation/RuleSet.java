package com.github.jazzschmidt.spring.jsonvalidation;

import java.util.HashMap;
import java.util.Map;

public class RuleSet {
    private final Map<String, Object> matchers = new HashMap<>();
    private final Map<String, Object> validators = new HashMap<>();
    private String name, description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMatchers() {
        return new HashMap<>(matchers);
    }

    public void setMatchers(Map<String, Object> matchers) {
        this.matchers.clear();
        this.matchers.putAll(matchers);
    }

    public Map<String, Object> getValidators() {
        return new HashMap<>(validators);
    }

    public void setValidators(Map<String, Object> validators) {
        this.validators.clear();
        this.validators.putAll(validators);
    }
}
