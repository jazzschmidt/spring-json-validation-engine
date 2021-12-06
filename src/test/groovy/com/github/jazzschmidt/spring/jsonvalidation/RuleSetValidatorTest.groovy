package com.github.jazzschmidt.spring.jsonvalidation

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jazzschmidt.spring.jsonvalidation.extra.TestConfiguration
import com.github.jazzschmidt.spring.jsonvalidation.extra.TestProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static com.github.jazzschmidt.spring.jsonvalidation.RuleSetBuilder.DefinitionsSupplier.fieldEquals

@SpringBootTest(classes = [TestConfiguration, JsonValidationAutoconfiguration, ObjectMapper])
class RuleSetValidatorTest extends Specification {

    @Autowired
    RuleSetValidator engine

    def "skips non-matching json"() {
        given:
        def json1 = ["id": 123456, "name": "foo"]
        def json2 = ["id": 111111, "name": "bar"]

        ruleSet {
            matchers = [fieldEquals('$.id', 123456), fieldEquals('$.name', "bar")]
            rules = [new TestProperties(success: true), new TestProperties(success: false)]
        }

        when:
        engine.validate(json1)
        engine.validate(json2)

        then:
        noExceptionThrown()
    }

    def "throws exception for matched json with failing rules"() {
        given:
        def json = ["id": 123456, "name": "foo"]

        ruleSet {
            matchers = [fieldEquals('$.id', 123456)]
            rules = [new TestProperties(success: true), new TestProperties(success: false)]
        }

        when:
        engine.validate(json)

        then:
        thrown(RuleValidationException)
    }

    private void ruleSet(@DelegatesTo(RuleSet) Closure configure) {
        def ruleSet = new RuleSet(name: "Test Name", description: "Test description")
        configure.delegate = ruleSet
        configure()

        engine.addRuleSet(ruleSet)
    }

}
