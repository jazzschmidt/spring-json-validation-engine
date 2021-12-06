package com.github.jazzschmidt.spring.jsonvalidation.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jazzschmidt.spring.jsonvalidation.JsonValidationAutoconfiguration
import com.github.jazzschmidt.spring.jsonvalidation.SchemaGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(classes = [JsonValidationAutoconfiguration, ObjectMapper])
class SchemaGeneratorTest extends Specification {

    final static String SCHEMA_MATCHERS = "/matchers-schema.json"
    final static String SCHEMA_RULES = "/rules-schema.json"

    @Autowired
    SchemaGenerator generator

    @Autowired
    ObjectMapper mapper

    def "loads rules and matches"() {
        expect:
        !generator.matcher.empty
        !generator.rules.empty
    }

    @Unroll("generates schema for #component")
    def "generates schema"() {
        given:
        def expectedSchema = mapper.readTree(file)

        when:
        def plainSchema = component == "matchers" ? generator.getMatchersSchema() : generator.getRulesSchema()
        def actualSchema = mapper.readTree(plainSchema)

        then:
        expectedSchema == actualSchema

        where:
        component  | file
        "matchers" | readFile(SCHEMA_MATCHERS)
        "rules"    | readFile(SCHEMA_RULES)
    }

    private String readFile(String path) {
        getClass().getResource(path).text
    }
}
