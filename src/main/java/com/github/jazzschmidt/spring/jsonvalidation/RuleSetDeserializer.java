package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Deserializes a {@link RuleSet} from JSON
 */
public class RuleSetDeserializer extends JsonDeserializer<RuleSet> {

    private final Map<String, Class<?>> matcherDefinitions = new HashMap<>();
    private final Map<String, Class<?>> ruleDefinitions = new HashMap<>();

    private final ObjectMapper internalMapper;

    private final TypeReference<Map<String, Object>> typeReferenceMap;

    {
        internalMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
        typeReferenceMap = new TypeReference<>() {
        };
    }

    public RuleSetDeserializer(Map<String, Class<?>> matcherDefinitions, Map<String, Class<?>> ruleDefinitions) {
        this.matcherDefinitions.putAll(matcherDefinitions);
        this.ruleDefinitions.putAll(ruleDefinitions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RuleSet deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        Map<String, Object> node = p.getCodec().readValue(p, typeReferenceMap);
        List<Map<String, Object>> nodeMatchers = (List<Map<String, Object>>) node.get("matchers");
        List<Map<String, Object>> nodeRules = (List<Map<String, Object>>) node.get("rules");

        RuleSet ruleSet = new RuleSet();
        ruleSet.setName((String) node.get("name"));
        ruleSet.setDescription((String) node.get("description"));

        List<Object> matchers = new ArrayList<>();
        List<Object> rules = new ArrayList<>();

        nodeMatchers.forEach(matcher -> {
            matchers.add(convertComponent(matcher, matcherDefinitions));
        });

        nodeRules.forEach(rule -> {
            rules.add(convertComponent(rule, ruleDefinitions));
        });

        ruleSet.setMatchers(matchers);
        ruleSet.setRules(rules);

        return ruleSet;
    }

    private Object convertComponent(Map<String, Object> component, Map<String, Class<?>> typeMappings) {
        String id = (String) component.remove("id");
        Class<?> type = typeMappings.get(id);

        if (type == null) {
            throw new NoSuchDefinitionException(id);
        }

        return internalMapper.convertValue(component, type);
    }
}
