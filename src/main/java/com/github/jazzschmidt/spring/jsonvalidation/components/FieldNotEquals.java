package com.github.jazzschmidt.spring.jsonvalidation.components;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.jazzschmidt.spring.jsonvalidation.JsonMatcher;
import com.github.jazzschmidt.spring.jsonvalidation.JsonRule;

/**
 * Definition for {@link FieldNotEqualsComponent}.
 */
@JsonMatcher(value = "field-not-equals-matcher", description = "Matches if a specific value is not present at a JSON path")
@JsonRule(value = "field-not-equals-rule", description = "Validates that a specific value is not present at a JSON path")
public class FieldNotEquals {
    /**
     * JSON Path of the property that should not hold the value of {@link #value}
     */
    @JsonPropertyDescription("JSON Path of the property that should not hold the value of `value`")
    public String jsonPath;

    /**
     * Value that should not be present in {@link #jsonPath}
     */
    @JsonPropertyDescription("Value that should not be present in `jsonPath`")
    public Object value;
}
