package com.github.jazzschmidt.spring.jsonvalidation.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;
import com.github.jazzschmidt.spring.jsonvalidation.SchemaGenerator;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * If not deactivates with {@code jsonvalidation.enableEndpoint} set to @{code false} in the application properties,
 * this controller exposes endpoints to create and list {@link RuleSet}s.
 * <p>
 * Furthermore, the JSON schema endpoints {@code /schema}, {@code /schema/matchers} and {@code /schema/rules} may be
 * used to assist in authoring {@link RuleSet}s. Every {@link RuleSet} being created will be validated against those
 * schemas.
 * </p>
 */
@RestController
@RequestMapping(value = "#{jsonValidationConfiguration.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "jsonvalidation.enableEndpoint", matchIfMissing = true)
public class RuleSetController {

    private final SchemaGenerator schemaGenerator;
    private final RuleSetService service;
    private final ObjectMapper objectMapper;

    @Autowired
    public RuleSetController(SchemaGenerator schemaGenerator, RuleSetService service, ObjectMapper objectMapper) {
        this.schemaGenerator = schemaGenerator;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the current URL, either as provided directly or via proxy - supposing the proxy adds the {@code
     * x-forwarded-*} headers.
     *
     * @param request HTTP request
     * @return Url of the current request
     */
    private static String getCurrentUrl(HttpServletRequest request) {
        String forwardedHost = request.getHeader("x-forwarded-host");

        if (forwardedHost == null) {
            return request.getRequestURL().toString();
        }

        String scheme = request.getHeader("x-forwarded-proto");
        String prefix = request.getHeader("x-forwarded-prefix");

        return scheme + "://" + forwardedHost + prefix + request.getRequestURI();
    }

    /**
     * Returns the root schema for {@link RuleSet}s.
     *
     * @param request HTTP request
     * @return JSON schema
     * @throws IOException if the root schema file could not be read
     */
    @GetMapping("/schema")
    public String getRootSchema(HttpServletRequest request) throws IOException {
        return getRootSchema(getCurrentUrl(request));
    }

    /**
     * Returns the root schema and replaces its url with {@code rootUrl}.
     *
     * @param rootUrl URL of the schema
     * @return JSON schema
     * @throws IOException if the root schema file could not be read
     */
    public String getRootSchema(String rootUrl) throws IOException {
        return schemaGenerator.getRootSchema(rootUrl);
    }

    /**
     * Returns the matchers schema.
     *
     * @return JSON schema
     * @throws JsonProcessingException if the schema could not be generated
     */
    @GetMapping("/schema/matchers")
    public String getMatchersSchema() throws JsonProcessingException {
        return schemaGenerator.getMatchersSchema();
    }

    /**
     * Returns the rules schema.
     *
     * @return JSON schema
     * @throws JsonProcessingException if the schema could not be generated
     */
    @GetMapping("/schema/rules")
    public String getRulesSchema() throws JsonProcessingException {
        return schemaGenerator.getRulesSchema();
    }

    /**
     * Creates a new {@link RuleSet} for validation.
     *
     * @param ruleSet Rule set
     * @param request HTTP request
     * @return List of all persisted {@link RuleSet}s
     * @throws IOException if any I/O error occurs
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<RuleSet> createRuleSet(@RequestBody RuleSet ruleSet, HttpServletRequest request) throws IOException {
        String schemaString = schemaGenerator.getMergedSchema(getCurrentUrl(request));
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = schemaFactory.getSchema(schemaString);

        validateRuleSet(ruleSet, schema);

        service.create(ruleSet);
        return service.getAll();
    }

    /**
     * Returns all {@link RuleSet}.
     *
     * @return List of {@link RuleSet}s
     */
    @GetMapping()
    public List<RuleSet> getRuleSets() {
        return service.getAll();
    }

    /**
     * Validates the given {@link RuleSet} against the root schema.
     *
     * @param ruleSet Rule set
     * @param schema  Rule set JSON schema
     */
    private void validateRuleSet(RuleSet ruleSet, JsonSchema schema) {
        JsonNode json = objectMapper.valueToTree(ruleSet);
        Set<ValidationMessage> messages = schema.validate(json);

        if (!messages.isEmpty()) {
            String msg = messages.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining("\n"));

            throw new IllegalArgumentException("JSON is invalid: \n" + msg);
        }
    }
}
