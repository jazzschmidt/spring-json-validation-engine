package com.github.jazzschmidt.spring.jsonvalidation.components;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.jazzschmidt.spring.jsonvalidation.JsonMatcher;
import com.github.jazzschmidt.spring.jsonvalidation.JsonRule;

/**
 * Definition for {@link FieldEqualsComponent}.
 */
@JsonMatcher(value = "field-equals-matcher", description = "Matches if a JSON path has a specific value")
@JsonRule(value = "field-equals-rule", description = "Validates that a specific value is present at a JSON path")
public class FieldEquals {
    /**
     * JSON Path of the property that shall hold the value of {@link #value}
     */
    @JsonPropertyDescription("JSON Path of the property that shall hold the value of `value`")
    public String jsonPath;

    /**
     * Value that shall be present in {@link #jsonPath}
     */
    @JsonPropertyDescription("Value that shall be present in `jsonPath`")
    public Object value;
}
