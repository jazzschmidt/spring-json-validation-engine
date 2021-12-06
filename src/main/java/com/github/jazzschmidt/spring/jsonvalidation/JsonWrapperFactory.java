package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Pending feature
 */
@Component
public class JsonWrapperFactory {

    private final ObjectMapper objectMapper;

    public JsonWrapperFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonWrapper wrap(JsonNode jsonNode) {
        return new JsonWrapper(objectMapper, jsonNode);
    }

    public JsonWrapper wrap(Map<String, Object> jsonMap) {
        return new JsonWrapper(objectMapper, jsonMap);
    }

}
