package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;

import java.util.*;

/**
 * Holds the {@link RuleSet} instances in memory. This is a very simple implementation that allows for overwriting
 * {@link RuleSet}s and does not permanently persist them.
 */
public class InMemoryRuleSetRepository implements RuleSetRepository {

    /**
     * Map of {@link RuleSet}s with their respective name
     */
    private final Map<String, RuleSet> ruleSets = new HashMap<>();

    @Override
    public RuleSet persist(RuleSet ruleSet) {
        Objects.requireNonNull(ruleSet.getName());
        Objects.requireNonNull(ruleSet.getDescription());

        ruleSets.put(ruleSet.getName(), ruleSet);
        return ruleSet;
    }

    @Override
    public List<RuleSet> getAll() {
        return new ArrayList<>(ruleSets.values());
    }

    @Override
    public Optional<RuleSet> getByName(String name) {
        return Optional.ofNullable(ruleSets.get(name));
    }

    @Override
    public void delete(RuleSet ruleSet) {
        deleteByName(ruleSet.getName());
    }

    @Override
    public void deleteByName(String name) {
        ruleSets.remove(name);
    }
}
