package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serializes a {@link RuleSet} to JSON
 */
public class RuleSetSerializer extends JsonSerializer<RuleSet> {

    private final ObjectMapper internalMapper;
    private final TypeReference<Map<String, Object>> typeReferenceMap;

    {
        internalMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
        typeReferenceMap = new TypeReference<>() {
        };
    }

    @Override
    public void serialize(RuleSet ruleSet, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        Map<String, Object> node = internalMapper.convertValue(ruleSet, typeReferenceMap);

        List<Map<String, Object>> matchers = new ArrayList<>();
        List<Map<String, Object>> rules = new ArrayList<>();

        // Append the definition id from the annotation to the object
        ruleSet.getMatchers().forEach(matcher -> {
            Map<String, Object> matcherNode = internalMapper.convertValue(matcher, typeReferenceMap);
            matcherNode.put("id", matcher.getClass().getAnnotation(JsonMatcher.class).value());
            matchers.add(matcherNode);
        });

        ruleSet.getRules().forEach(rule -> {
            Map<String, Object> ruleNode = internalMapper.convertValue(rule, typeReferenceMap);
            ruleNode.put("id", rule.getClass().getAnnotation(JsonRule.class).value());
            rules.add(ruleNode);
        });

        // Overwrite the definitions with the ones having an id
        node.put("matchers", matchers);
        node.put("rules", rules);

        gen.writeObject(node);
    }
}
