package com.github.jazzschmidt.spring.jsonvalidation

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jazzschmidt.spring.jsonvalidation.components.FieldEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static com.github.jazzschmidt.spring.jsonvalidation.RuleSetBuilder.DefinitionsSupplier.fieldEquals

@SpringBootTest(classes = [JsonValidationAutoconfiguration, ObjectMapper])
class RuleSetDeserializerTest extends Specification {

    @Autowired
    private ObjectMapper mapper

    def "deserializes rule set components"() {
        given:
        def ruleSet = new RuleSet()
        ruleSet.name = ruleSet.description = "Test validator set"
        ruleSet.matchers = [fieldEquals('$.id', 1)]
        ruleSet.rules = [fieldEquals('$.id', 2)]

        def json = mapper.writeValueAsString(ruleSet)

        when:
        def actual = mapper.readValue(json, RuleSet)

        then:
        actual.matchers.size() == 1
        actual.rules.size() == 1

        def actualMatcher = actual.matchers.first() as FieldEquals
        def actualRule = actual.rules.first() as FieldEquals

        actualMatcher.jsonPath == '$.id'
        actualMatcher.value == 1
        actualRule.jsonPath == '$.id'
        actualRule.value == 2
    }

}
