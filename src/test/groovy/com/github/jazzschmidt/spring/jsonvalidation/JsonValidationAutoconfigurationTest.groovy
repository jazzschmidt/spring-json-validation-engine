package com.github.jazzschmidt.spring.jsonvalidation

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jazzschmidt.spring.jsonvalidation.components.FieldEquals
import com.github.jazzschmidt.spring.jsonvalidation.components.FieldNotEmpty
import com.github.jazzschmidt.spring.jsonvalidation.components.FieldNotEquals
import com.github.jazzschmidt.spring.jsonvalidation.extra.TestConfiguration
import com.github.jazzschmidt.spring.jsonvalidation.extra.TestProperties
import com.github.jazzschmidt.spring.jsonvalidation.web.RuleSetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [JsonValidationAutoconfiguration, ObjectMapper, TestConfiguration])
class JsonValidationAutoconfigurationTest extends Specification {

    @Autowired
    RuleSetRepository ruleSetRepository

    @Autowired
    Map<String, Class<?>> matchers

    @Autowired
    Map<String, Class<?>> rules

    def 'context starts'() {
        expect:
        matchers == [
                'field-equals-matcher'    : FieldEquals,
                'field-not-equals-matcher': FieldNotEquals,
                'field-not-empty-matcher' : FieldNotEmpty,
                'test-matcher'            : TestProperties
        ]

        rules == [
                'field-equals-rule'    : FieldEquals,
                'field-not-equals-rule': FieldNotEquals,
                'field-not-empty-rule' : FieldNotEmpty,
                'test-rule'            : TestProperties
        ]
    }

}
