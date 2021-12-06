package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;

import java.util.List;
import java.util.Optional;

/**
 * Persists and reads {@link RuleSet}s.
 */
public interface RuleSetRepository {

    /**
     * Persists a {@link RuleSet}.
     *
     * @param ruleSet rule set
     * @return persisted rule set
     */
    RuleSet persist(RuleSet ruleSet);

    /**
     * Retrieves all {@link RuleSet}s.
     *
     * @return
     */
    List<RuleSet> getAll();

    /**
     * Returns a {@link RuleSet} by its name.
     *
     * @param name Name of the rule set
     * @return Rule set
     */
    Optional<RuleSet> getByName(String name);

    /**
     * Deletes the {@link RuleSet}.
     *
     * @param ruleSet rule set
     */
    void delete(RuleSet ruleSet);

    /**
     * Deletes the {@link RuleSet} by its name.
     *
     * @param name
     */
    void deleteByName(String name);

}
