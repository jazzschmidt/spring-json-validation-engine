# Spring JSON Validation Engine

Aspect oriented validation of incoming JSON content using a rule engine
that can be configured on startup and expanded via REST at runtime.

## Features
- decouples validation from controller handler
- rule engine is expandable at runtime
- generates JSON schemas for your own components
- very simple configuration

See [REST API](#rest-api) for a simple overview of its usage.

## Semantics

A `RuleSet` consists of **definitions** that describe a set of rules for
matching against JSON and validating it. Precisely, a definition
configures a `RuleSetComponent` - that is either a **Matcher**
or a **Validator** and in fact a `@Component` bean.

**Components:**

- `Matcher`
    specifiy if a **RuleSet** should be applied on a JSON object
- `Validator`
    validates the object if *all Matchers* were successfull

## Configuration

The engines REST API can be configured under the `jsonvalidation` key via the application properties.

|Key|Default|Description|
|-|-|-|
|`enable-endpoint`|`true`|Enables the REST API|
|`endpoint`|`/jsonvalidation`|URL of that REST API|

To enable custom components and definitions simply add the `@JsonValidationComponents`
annotation to one of your configuration classes:

```java
@SpringBootApplication
// Use all definitions and components from the current package and its subpackages
@JsonValidationComponents
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

## Adding Rules

You can either promatically add a ``RuleSet`` or use its JSON representation and
the REST API to add it to a running application.

### Programmatically

There is a convenient `RuleSetBuilder`, that can be used to generate RuleSets and
add them to the `RuleSetRepository` or directly to the `RuleSetValidator`. By doing the latter,
those RuleSets are not exposed and cannot be managed via REST.

```java
import com.github.jazzschmidt.spring.jsonvalidation.RuleSet;
import com.github.jazzschmidt.spring.jsonvalidation.RuleSetBuilder;
import com.github.jazzschmidt.spring.jsonvalidation.RuleSetValidator;

import static com.github.jazzschmidt.spring.jsonvalidation.RuleSetBuilder.DefinitionsSupplier.*;

@Configuration
class RuleSetConfiguration {
    private final RuleSetValidator validator;
    
    @Autowired
    public RuleSetConfiguration(RuleSetValidator validator) {
        this.validator = validator;
        initRules();
    }
    
    private void initRules() {
        String description = "Test validation";
        
        RuleSet ruleSet = new RuleSetBuilder("Example RuleSet", description)
            .when()
            .matches(fieldEquals("$.type", "example"))
            .matches(fieldEquals("$.id", 2))
            .then()
            .validates(fieldEquals("$.name", "Example Name"))
            .validates(fieldNotEmpty("$.author"))
            .build();
    
        validator.addRuleSet(ruleSet);
    }
}
```

### REST API

There is a simple REST API (default endpoint `/jsonvalidation`) that enables you to
create and view the active rules in the validation engine. You can also retrieve
the JSON schema to interactively build the rules in your favourite editor and don't need
to remember the syntax everytime.

- `POST`: creates new RuleSets
- `GET`: lists all active RuleSets
- `GET` on `/schema`: JSON schema of the RuleSets
- `GET` on `/schema/[matchers|rules]`: JSON schema of the definitions

**Example RuleSet**:
```json
{
  "name": "Example RuleSet",
  "description": "Test validation",
  "matchers": [
    {
      "id": "field-equals-matcher",
      "jsonPath": "$.type",
      "value": "example"
    },
    {
      "id": "field-equals-matcher",
      "jsonPath": "$.id",
      "value": 2
    } 
  ],
  "rules": [
    {
      "id": "field-equals-rule",
      "jsonPath": "$.name",
      "value": "Example Name"
    },
    {
      "id": "field-not-empty-rule",
      "jsonPath": "$.author"
    }
  ]
}
```

## Applying Rules

Use the `RuleSetValidator` to validate an object against all matching RuleSets:

```java
void validateJson(@Autowired RuleSetValidator validator) throws RuleValidationException {
    validator.validate(this.json)
}
```

Annotate a REST controller handler with `@ValidateJsonContent` to magically
validate its incoming JSON. The following snippet validates the request against all RuleSets
and simply returns the content when no one failed:

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping
    @ValidateJsonContent // magic
    public Map<String, Object> post(@RequestBody Map<String, Object> content) {
        return content;
    }

}
```

Using the [previously defined RuleSet](#applying-rules) and sending invalid JSON leads
to a HTTP status **FORBIDDEN** and this error message:

**Request:**
```json
{
  "id": 2,
  "type": "example",
  "name": "Foobar",
  "author": "user"
}
```

**Response:**
```json
{
  "message": "Value of $.name must be `Example Name`, but is `Foobar`",
  "ruleSet": {
    "name": "Example RuleSet",
    "description": "Test validation"
  }
}
```

# Adding Components

If [custom components are enabled](#configuration), you can simply add your own **Matchers**
and **Validators**, that are not limited to syntactic validation but can validate using
extended business logic. A component that shall be used both as Matcher and Validator, just
like `field-equals`, is considered a _native component_.

A component also needs a serializable **Definition** class to be parameterised accordingly.
The definition for `field-equals` is straight-foreward:

```java
@JsonMatcher(value = "field-equals-matcher", description = "Matches if a JSON path has a specific value")
@JsonRule(value = "field-equals-rule", description = "Validates that a specific value is present at a JSON path")
public class FieldEquals {
    @JsonPropertyDescription("JSON Path of the property that shall hold the value of `value`")
    public String jsonPath;

    @JsonPropertyDescription("Value that shall be present in `jsonPath`")
    public Object value;
}
```

The matcher JSON schema at `/jsonvalidation/schema/matcher` then generates the following snippet:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "JSON validation matchers schema",
  "type": "object",
  "definitions": {
    "field-equals-matcher": {
      "description": "Matches if a JSON path has a specific value",
      "required": [
        "id",
        "jsonPath",
        "value"
      ],
      "properties": {
        "id": {
          "type": "string",
          "const": "field-equals-matcher"
        },
        "jsonPath": {
          "description": "JSON Path of the property that shall hold the value of `value`",
          "type": "string"
        },
        "value": {
          "description": "Value that shall be present in `jsonPath`"
        }
      }
    }
  }
}
```

Since the [FieldEqualsComponent] can be used for both matching and validating JSON, it is a
native component.
The default behaviour of applying the rule (`apply(definition, json)`) is simply matching
(`matches(definition, json)`) the JSON and throwing the provided exception in case the result is `false`.

```java
@Component
public class FieldEqualsComponent extends NativeRuleSetComponent<FieldEquals> {

    public FieldEqualsComponent() {
        super(FieldEquals.class);
    }

    @Override
    public boolean matches(FieldEquals definition, RuleSetValidator.JsonWrapper json) {
        Object value = readJsonPath(definition.jsonPath, json);
        return value != null && value.equals(definition.value);
    }

    // Will be thrown when #apply returns false
    @Override
    @NonNull
    protected RuleValidationException validationException(FieldEquals definition, RuleSetValidator.JsonWrapper json) {
        Object value = readJsonPath(definition.jsonPath, json);
        String message = String.format("Value of %s must be `%s`, but is `%s`", definition.jsonPath, definition.value, value);

        return new RuleValidationException(message);
    }

}
```

The JsonWrapper can be used to retrieve the JSON as `JsonNode`, `Map<String, Object>` or simply as `String`.
