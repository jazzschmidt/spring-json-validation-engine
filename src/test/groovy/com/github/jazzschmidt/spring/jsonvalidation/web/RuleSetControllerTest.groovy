package com.github.jazzschmidt.spring.jsonvalidation.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jazzschmidt.spring.jsonvalidation.RuleSet
import com.github.jazzschmidt.spring.jsonvalidation.RuleSetValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spock.lang.Specification

import static com.github.jazzschmidt.spring.jsonvalidation.RuleSetBuilder.DefinitionsSupplier.fieldEquals
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RuleSetControllerTest extends Specification {

    @SpringBootApplication
    @ComponentScan("com.github.jazzschmidt.spring.jsonvalidation")
    static class SpringContext {}

    @Autowired
    private RuleSetValidator validator

    @Autowired
    private ObjectMapper mapper

    @Autowired
    private MockMvc mvc

    def "creates a new RuleSet"() {
        given:
        def ruleSet = new RuleSet()
        ruleSet.name = ruleSet.description = "Test validator set"
        ruleSet.matchers = [fieldEquals('$.id', 1)]
        ruleSet.rules = [fieldEquals('$.name', 'Foo')]

        def json = mapper.writeValueAsString(ruleSet)

        expect:
        validator.ruleSets.empty

        mvc.perform(get("/jsonvalidation")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[ ]"))

        mvc.perform(post("/jsonvalidation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json("[ $json ]"))

        !validator.ruleSets.empty
    }

    def "passes valid json objects to controller"() {
        given:
        def ruleSet = new RuleSet()
        ruleSet.name = ruleSet.description = "Test validator set"
        ruleSet.matchers = [fieldEquals('$.id', 1)]
        ruleSet.rules = [fieldEquals('$.name', 'Foo')]

        def goodJson = [
                [id: 1, name: 'Foo'],
                [id: 2, name: 'Baz']
        ].collect { mapper.writeValueAsString(it) }

        when:
        mvc.perform(post("/jsonvalidation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ruleSet)))
                .andExpect(status().isOk())

        goodJson.each { json ->
            mvc.perform(post("/test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json))
        }

        then:
        noExceptionThrown()
    }

    def "throws error when passing invalid json object to controller"() {
        given:
        def ruleSet = new RuleSet()
        ruleSet.name = ruleSet.description = "Test rule set"
        ruleSet.matchers = [fieldEquals('$.id', 1)]
        ruleSet.rules = [fieldEquals('$.name', 'Foo')]

        def badJson = [
                [id: 1, name: 'Foobar'],
                [id: 1, name: 'Baz']
        ].collect { mapper.writeValueAsString(it) }

        when:
        mvc.perform(post("/jsonvalidation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ruleSet)))
                .andExpect(status().isOk())

        badJson.each { json ->
            mvc.perform(post("/test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        }

        then:
        noExceptionThrown()
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @PostMapping()
        @ValidateJsonContent
        HashMap<String, Object> post(@RequestBody HashMap<String, Object> json) {
            return json
        }
    }

}
