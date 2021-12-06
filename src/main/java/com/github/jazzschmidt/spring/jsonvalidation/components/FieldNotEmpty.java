package com.github.jazzschmidt.spring.jsonvalidation.components;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.jazzschmidt.spring.jsonvalidation.JsonMatcher;
import com.github.jazzschmidt.spring.jsonvalidation.JsonRule;

/**
 * Definition for {@link FieldNotEmptyComponent}.
 */
@JsonMatcher(value = "field-not-empty-matcher", description = "Matches if a JSON path is non-empty")
@JsonRule(value = "field-not-empty-rule", description = "Validates that a JSON path is non-empty")
public class FieldNotEmpty {
    /**
     * JSON Path of the property that shall hold any value other than null
     */
    @JsonPropertyDescription("JSON Path of the property that shall hold any value other than null")
    public String jsonPath;
}
