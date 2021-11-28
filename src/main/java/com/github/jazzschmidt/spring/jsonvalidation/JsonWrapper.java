package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Wrapper for convenient retrieval of lazy-generated views of the JSON object.
 */
public final class JsonWrapper {
    private final ObjectMapper objectMapper;
    private final TypeReference<Map<String, Object>> mapReference = new TypeReference<>() {
    };
    private JsonNode jsonNode;
    private Map<String, Object> jsonMap;
    private String jsonText;

    public JsonWrapper(JsonNode jsonNode) {
        this(new ObjectMapper(), jsonNode);
    }

    public JsonWrapper(Map<String, Object> jsonMap) {
        this(new ObjectMapper(), jsonMap);
    }

    public JsonWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
        this.objectMapper = objectMapper;
        this.jsonNode = jsonNode;
    }

    public JsonWrapper(ObjectMapper objectMapper, Map<String, Object> jsonMap) {
        this.objectMapper = objectMapper;
        this.jsonMap = jsonMap;
    }

    /**
     * Gets the JSON as {@link JsonNode}
     *
     * @return JSON
     */
    public JsonNode getJsonNode() {
        if (jsonNode == null && jsonMap != null) {
            jsonNode = objectMapper.valueToTree(jsonMap);
        }

        return jsonNode;
    }

    /**
     * Gets the JSON as Map
     *
     * @return JSON
     */
    public Map<String, Object> getJsonMap() {
        if (jsonMap == null && jsonNode != null) {
            jsonMap = objectMapper.convertValue(jsonNode, mapReference);
        }

        return jsonMap;
    }

    /**
     * Gets the JSON as String
     *
     * @return JSON
     * @throws JsonProcessingException if the JSON could not be converted
     */
    public String getJsonText() throws JsonProcessingException {
        if (jsonText == null) {
            Object from = jsonNode == null ? jsonMap : jsonNode;
            jsonText = objectMapper.writeValueAsString(from);
        }

        return jsonText;
    }
}
