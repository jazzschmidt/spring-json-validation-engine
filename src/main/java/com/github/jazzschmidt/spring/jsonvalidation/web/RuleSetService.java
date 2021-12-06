package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;
import com.github.jazzschmidt.spring.jsonvalidation.RuleSetValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Attaches and removes {@link RuleSet}s to the {@link RuleSetValidator} and persists them in the {@link
 * RuleSetRepository}.
 */
@Service
@ConditionalOnWebApplication
public class RuleSetService {

    private final RuleSetRepository repository;
    private final RuleSetValidator validator;

    @Autowired
    public RuleSetService(RuleSetRepository repository, RuleSetValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Creates a {@link RuleSet} and attaches it to the {@link RuleSetValidator}.
     *
     * @param ruleSet rule set
     * @return persisted rule set
     */
    public RuleSet create(@NotNull RuleSet ruleSet) {
        repository.persist(ruleSet);
        validator.addRuleSet(ruleSet);

        return ruleSet;
    }

    /**
     * Returns all persisted {@link RuleSet}s.
     *
     * @return List of rule sets
     */
    public List<RuleSet> getAll() {
        return repository.getAll();
    }

    /**
     * Removes a {@link RuleSet} by its name.
     *
     * @param name Name of the {@link RuleSet}
     * @throws NoSuchElementException if no value is present
     */
    public void remove(@NotNull String name) {
        RuleSet ruleSet = repository.getByName(name).get();
        repository.delete(ruleSet);
        validator.removeRuleSet(ruleSet);
    }

}
