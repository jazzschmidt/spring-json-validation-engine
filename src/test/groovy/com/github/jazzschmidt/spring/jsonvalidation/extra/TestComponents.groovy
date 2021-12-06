package com.github.jazzschmidt.spring.jsonvalidation.extra

import com.github.jazzschmidt.spring.jsonvalidation.*
import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@JsonValidationComponents
@ComponentScan
class TestConfiguration {

}

@JsonMatcher("test-matcher")
@JsonRule("test-rule")
class TestProperties {
    boolean success
}

@TestComponent
class TestMatcher extends Matcher<TestProperties> {
    TestMatcher() {
        super(TestProperties)
    }

    @Override
    protected boolean matches(TestProperties definition, JsonWrapper json) {
        return definition.success
    }
}

@TestComponent
class TestValidator extends Validator<TestProperties> {
    TestValidator() {
        super(TestProperties)
    }

    @Override
    void apply(TestProperties definition, JsonWrapper json) {
        if (!definition.success)
            throw new RuleValidationException("Failed")
    }
}

