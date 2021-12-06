package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Generates JSON schemas of the {@link JsonMatcher}s and {@link JsonRule}s
 */
public class SchemaGenerator {
    private final static String SCHEMA_ROOT = "/validation-rules-schema.json";
    private final Map<String, Class<?>> matchers = new HashMap<>();
    private final Map<String, Class<?>> rules = new HashMap<>();
    private final ObjectMapper objectMapper;
    private final Map<Class<?>, String> simpleTypeMappings = new HashMap<>();

    {
        simpleTypeMappings.put(String.class, "string");
        simpleTypeMappings.put(Boolean.class, "boolean");
        simpleTypeMappings.put(Integer.class, "integer");
        simpleTypeMappings.put(Number.class, "number");
        simpleTypeMappings.put(List.class, "array");
    }

    SchemaGenerator(Map<String, Class<?>> matchers, Map<String, Class<?>> rules, ObjectMapper objectMapper) {
        this.matchers.putAll(matchers);
        this.rules.putAll(rules);
        this.objectMapper = objectMapper;
    }

    private static String readSchemaFromResource() throws IOException {
        ClassPathResource res = new ClassPathResource(SCHEMA_ROOT);
        return new BufferedReader(new InputStreamReader(res.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public Map<String, Class<?>> getRules() {
        return rules;
    }

    public Map<String, Class<?>> getMatcher() {
        return matchers;
    }

    /**
     * Returns the root schema of {@link RuleSet}s
     *
     * @param url Url of the schema
     * @return JSON schema
     * @throws IOException if any I/O error occurs
     */
    public String getRootSchema(String url) throws IOException {
        String rootSchema = readSchemaFromResource();

        return rootSchema.replace("{{ URL }}", url);
    }

    /**
     * Returns the root schema of {@link RuleSet}s merged with all {@link JsonMatcher}s and {@link JsonRule}s. For
     * internal purposes; not intended to be the main schema.
     *
     * @param url Url if the schema
     * @return JSON schema
     * @throws IOException if any I/O error occurs
     */
    @SuppressWarnings("unchecked")
    public String getMergedSchema(String url) throws IOException {
        String rootSchema = readSchemaFromResource();
        rootSchema = rootSchema.replace("{{ URL }}", url);
        rootSchema = rootSchema.replaceAll("/(matchers|rules)#", "#"); // Remove links

        Map<String, Object> json = (Map<String, Object>) objectMapper.readValue(rootSchema, Map.class);
        Map<String, Object> definitions = (Map<String, Object>) json.get("definitions");

        Consumer<Map<String, Object>> addDefinitions = schema -> {
            definitions.putAll((Map<? extends String, ?>) schema.get("definitions"));
        };

        addDefinitions.accept(createSchema(matchers, ComponentType.Matcher));
        addDefinitions.accept(createSchema(rules, ComponentType.Rule));

        return objectMapper.writeValueAsString(rootSchema);
    }

    /**
     * Returns the generated JSON schema of all {@link JsonMatcher}s
     *
     * @return JSON schema
     * @throws JsonProcessingException if any I/O error occurs
     */
    public String getMatchersSchema() throws JsonProcessingException {
        Map<String, Object> schema = createSchema(matchers, ComponentType.Matcher);

        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(schema);
    }

    /**
     * Returns the generated JSON schema of all {@link JsonRule}s
     *
     * @return JSON schema
     * @throws JsonProcessingException if any I/O error occurs
     */
    public String getRulesSchema() throws JsonProcessingException {
        Map<String, Object> schema = createSchema(rules, ComponentType.Rule);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
    }

    /**
     * Appends JSON schema property definitions and required properties to the component definition.
     *
     * @param properties   JSON properties
     * @param definition   Match/Rule definition
     * @param definitionId unique id if the definition
     */
    @SuppressWarnings("unchecked")
    private void appendProperties(List<BeanPropertyDefinition> properties, Map<String, Object> definition,
                                  String definitionId) {
        List<String> required = (List<String>) definition.get("required");
        Map<String, Object> definitions = (Map<String, Object>) definition.get("properties");

        {
            // Add required id field
            Map<String, Object> id = new LinkedHashMap<>();
            id.put("type", "string");
            id.put("const", definitionId);

            definitions.put("id", id);
            required.add("id");
        }

        for (BeanPropertyDefinition property : properties) {
            String fieldName = property.getName();
            String simpleType = getSimpleType(property.getRawPrimaryType());

            Map<String, Object> jsonDefinition = new LinkedHashMap<>();

            AnnotatedMember accessor = property.getAccessor();

            if (accessor == null) {
                continue;
            }

            JsonPropertyDescription description = accessor.getAnnotation(JsonPropertyDescription.class);
            if (description != null) {
                jsonDefinition.put("description", description.value());
            }

            if (simpleType != null) {
                jsonDefinition.put("type", simpleType);
            }

            definitions.put(fieldName, jsonDefinition);
            required.add(fieldName);
        }
    }

    /**
     * Collects the JSON properties of a class as seen by Jackson
     *
     * @param clazz Class to be inspected
     * @return List of {@link BeanPropertyDefinition}s
     */
    private List<BeanPropertyDefinition> getJsonProperties(Class<?> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();

        // Introspect the bean
        BeanDescription beanDescription = serializationConfig.introspect(javaType);

        // Get class level ignored properties
        Set<String> ignoredProperties = serializationConfig.getAnnotationIntrospector()
                .findPropertyIgnorals(beanDescription.getClassInfo()).getIgnored();

        // Find all serializable properties
        return beanDescription.findProperties().stream()
                .filter(property -> !ignoredProperties.contains(property.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get the JSON simple type name of the goven class
     *
     * @param clazz Class object
     * @return Simple type name
     */
    private String getSimpleType(Class<?> clazz) {
        return simpleTypeMappings.get(clazz);
    }

    /**
     * Create the schema for component definitions.
     *
     * @param definitions Matcher/Rule definitions
     * @param type        {@link JsonMatcher} or {@link JsonRule}
     * @return JSON schema
     */
    private Map<String, Object> createSchema(Map<String, Class<?>> definitions, ComponentType type) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("$schema", "http://json-schema.org/draft-07/schema#");
        schema.put("title", type.description);
        schema.put("type", "object");

        Map<String, Object> jsonDefinitions = new HashMap<>();
        List<String> allIds = new ArrayList<>();

        for (Map.Entry<String, Class<?>> definition : definitions.entrySet()) {
            String id = definition.getKey();
            Class<?> clazz = definition.getValue();

            allIds.add(id);

            // JSON Schema chunk of this definition
            Map<String, Object> jsonDefinition = new LinkedHashMap<>();

            // Add the description if present
            String description;
            if (type == ComponentType.Matcher) {
                description = clazz.getAnnotation(JsonMatcher.class).description();
            } else {
                description = clazz.getAnnotation(JsonRule.class).description();
            }
            if (!description.isEmpty()) {
                jsonDefinition.put("description", description);
            }

            jsonDefinition.put("required", new ArrayList<String>());
            jsonDefinition.put("properties", new LinkedHashMap<String, Object>());

            List<BeanPropertyDefinition> properties = getJsonProperties(clazz);
            appendProperties(properties, jsonDefinition, id);

            jsonDefinitions.put(id, jsonDefinition);
        }

        // Use alphabetically sorted definitions map
        Map<String, Object> sortedDefinitions = new LinkedHashMap<>();
        jsonDefinitions.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> sortedDefinitions.put(entry.getKey(), entry.getValue()));

        // Append the reference type alias
        Map<String, Object> refType = new LinkedHashMap<>();
        refType.put("type", "object");
        refType.put("oneOf", allIds.stream()
                .sorted()
                .map(name -> Map.of("$ref", String.format("#/definitions/%s", name)))
                .collect(Collectors.toList())
        );

        sortedDefinitions.put(type.definitionsGroup, refType);

        schema.put("definitions", sortedDefinitions);

        return schema;
    }

    private enum ComponentType {
        Matcher("matcher", "JSON validation matchers schema"),
        Rule("rules", "JSON validation rules schema");

        private final String definitionsGroup;
        private final String description;

        ComponentType(String definitionsGroup, String description) {
            this.definitionsGroup = definitionsGroup;
            this.description = description;
        }
    }

}
