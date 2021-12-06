package com.github.jazzschmidt.spring.jsonvalidation;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.springframework.lang.Nullable;

/**
 * Base class of the JSON Validation {@link Matcher}s and {@link Validator}s.
 */
abstract public class RuleSetComponent {

    /**
     * Retrieve a value from the given JSON that can be nullable.
     *
     * @param jsonPath JSON path
     * @param json     JSON
     * @return its value or null
     */
    @Nullable
    protected Object readJsonPath(String jsonPath, JsonWrapper json) {
        Configuration config = Configuration.defaultConfiguration().
                setOptions(Option.SUPPRESS_EXCEPTIONS);

        return JsonPath.using(config).parse(json.getJsonMap()).read(jsonPath);
    }
}
