package com.github.jazzschmidt.spring.jsonvalidation;

import org.springframework.lang.NonNull;

/**
 * Combines a {@link Matcher} and {@link Validator} in a single class for coveinience.
 *
 * @param <DefinitionType> class of the definition
 */
abstract public class NativeRuleSetComponent<DefinitionType> extends RuleSetComponent {

    private final Matcher<DefinitionType> matcher;
    private final Validator<DefinitionType> validator;

    public NativeRuleSetComponent(Class<DefinitionType> targetType) {
        matcher = new ComponentMatcher(this, targetType);
        validator = new ComponentValidator(this, targetType);
    }

    /**
     * Returns the {@link Matcher}
     *
     * @return Matcher
     */
    public Matcher<DefinitionType> getMatcher() {
        return matcher;
    }

    /**
     * Returns the {@link Validator}
     *
     * @return Validator
     */
    public Validator<DefinitionType> getValidator() {
        return validator;
    }

    /**
     * Matches the JSON against this matcher with the configuration of the {@code definition} object.
     *
     * @param definition configuration of this match
     * @param json       JSON
     * @return true if the JSON matches
     */
    public abstract boolean matches(DefinitionType definition, JsonWrapper json);

    /**
     * Returns the {@link RuleValidationException} that shall be used when the validation fails.
     *
     * @param definition configuration of the validation
     * @param json       JSON
     * @return Exception to be thrown
     */
    @NonNull
    protected abstract RuleValidationException validationException(DefinitionType definition, JsonWrapper json);

    /**
     * Throws the {@link RuleValidationException} from {@link #validationException(Object, JsonWrapper)} if the match
     * fails with the given definition.
     *
     * @param definition configuration of the validation
     * @param json       JSON
     * @throws RuleValidationException if the validation fails
     */
    protected void apply(DefinitionType definition, JsonWrapper json) throws RuleValidationException {
        if (!matches(definition, json)) {
            throw validationException(definition, json);
        }
    }

    /**
     * Nested matcher instance that delegates to the {@link NativeRuleSetComponent}.
     */
    public final class ComponentMatcher extends Matcher<DefinitionType> {
        private final NativeRuleSetComponent<DefinitionType> delegate;

        public ComponentMatcher(NativeRuleSetComponent<DefinitionType> delegate, Class<DefinitionType> targetType) {
            super(targetType);
            this.delegate = delegate;
        }

        @Override
        protected boolean matches(DefinitionType definition, JsonWrapper json) {
            return delegate.matches(definition, json);
        }
    }

    /**
     * Nested Validator instance that delegates to the {@link NativeRuleSetComponent}.
     */
    public final class ComponentValidator extends Validator<DefinitionType> {
        private final NativeRuleSetComponent<DefinitionType> delegate;

        public ComponentValidator(NativeRuleSetComponent<DefinitionType> delegate, Class<DefinitionType> targetType) {
            super(targetType);
            this.delegate = delegate;
        }

        @Override
        protected void apply(DefinitionType definition, JsonWrapper json) throws RuleValidationException {
            delegate.apply(definition, json);
        }
    }

}
