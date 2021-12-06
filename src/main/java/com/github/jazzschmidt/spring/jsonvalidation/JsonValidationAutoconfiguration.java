package com.github.jazzschmidt.spring.jsonvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.jazzschmidt.spring.jsonvalidation.web.InMemoryRuleSetRepository;
import com.github.jazzschmidt.spring.jsonvalidation.web.InterceptorConfigurer;
import com.github.jazzschmidt.spring.jsonvalidation.web.RuleSetRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Enables the autoconfiguration of the JSON Validation library. Will inject combined {@link Matcher}s and {@link
 * Validator}s of {@link NativeRuleSetComponent}s afterwards and therefore needs access to the {@link BeanFactory}.
 */
@Configuration
@Import({JsonValidationConfiguration.class, InterceptorConfigurer.class})
// Scan Spring components in this package
@ComponentScan
public class JsonValidationAutoconfiguration implements BeanFactoryAware {

    /**
     * Collection of matcher definitions with their respective id
     */
    private final Map<String, Class<?>> matcherDefinitions = new HashMap<>();

    /**
     * Collection of rule definitions with their respective id
     */
    private final Map<String, Class<?>> ruleDefinitions = new HashMap<>();

    /**
     * Base packages that will be scanned by the {@link JsonValidationComponents} annotation for collecting the
     * definitions and components
     */
    private final List<String> basePackages = new ArrayList<>();

    private final ApplicationContext ctx;
    private final ObjectMapper objectMapper;
    private BeanFactory beanFactory;

    @Autowired
    public JsonValidationAutoconfiguration(ApplicationContext ctx, ObjectMapper objectMapper) throws ClassNotFoundException {
        this.ctx = ctx;
        this.objectMapper = objectMapper;
        initialize();
    }

    /**
     * Collects all definitions per classpath scanning and configures the ObjectMapper
     *
     * @throws ClassNotFoundException
     */
    private void initialize() throws ClassNotFoundException {
        collectBasePackages();
        collectDefinitions();
        configureObjectMapper();
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Collects all instances of {@link NativeRuleSetComponent} beans and injects their {@link
     * NativeRuleSetComponent#getMatcher()} and {@link NativeRuleSetComponent#getValidator()} into the bean
     * configuration.
     */
    @SuppressWarnings("rawtypes")
    @PostConstruct
    public void collectNativeComponents() {
        ConfigurableBeanFactory beans = (ConfigurableBeanFactory) beanFactory;
        Map<String, NativeRuleSetComponent> beansWithAnnotation = ctx.getBeansOfType(NativeRuleSetComponent.class);

        for (Map.Entry<String, NativeRuleSetComponent> entry : beansWithAnnotation.entrySet()) {
            String name = entry.getKey();
            NativeRuleSetComponent<?> bean = entry.getValue();

            // Append the type to the bean name
            beans.registerSingleton(name + "Matcher", bean.getMatcher());
            beans.registerSingleton(name + "Validator", bean.getValidator());
        }
    }

    /**
     * Collects the {@link JsonMatcher} and {@link JsonRule} annotated classes, that shall be used as component
     * definitions. Classpath scanning can be extended with the {@link JsonValidationComponents} annotation in the
     * respective package.
     *
     * @throws ClassNotFoundException
     */
    private void collectDefinitions() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(true);

        scanner.addIncludeFilter(new AnnotationTypeFilter(JsonMatcher.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(JsonRule.class));

        for (String basePackage : basePackages) {
            for (BeanDefinition definition : scanner.findCandidateComponents(basePackage)) {
                Class<?> clazz = Class.forName(definition.getBeanClassName());

                JsonMatcher jsonMatcher = clazz.getAnnotation(JsonMatcher.class);
                JsonRule jsonRule = clazz.getAnnotation(JsonRule.class);

                if (jsonMatcher != null) {
                    matcherDefinitions.put(jsonMatcher.value(), clazz);
                }

                if (jsonRule != null) {
                    ruleDefinitions.put(jsonRule.value(), clazz);
                }
            }
        }
    }

    /**
     * Collects the base packages that will be used for d scanning
     */
    private void collectBasePackages() {
        // Most likely Configuration-Beans such as @SpingBootApplication
        Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(JsonValidationComponents.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();

            JsonValidationComponents components = ctx.findAnnotationOnBean(name, JsonValidationComponents.class);
            assert components != null;
            String basePackage = components.basePackage();

            // Add the package to the scanning scope
            basePackages.add(basePackage.isEmpty() ? bean.getClass().getPackageName() : basePackage);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired
    public RuleSetValidator ruleSetValidator(Set<Matcher<?>> matchers, Set<Validator<?>> validators) {
        return new RuleSetValidator(matchers, validators);
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleSetRepository ruleSetRepository() {
        return new InMemoryRuleSetRepository();
    }

    @Bean
    public Map<String, Class<?>> matchers() {
        return new HashMap<>(matcherDefinitions);
    }

    @Bean
    public Map<String, Class<?>> rules() {
        return new HashMap<>(ruleDefinitions);
    }

    /**
     * Adds the {@link RuleSetSerializer} and {@link RuleSetDeserializer} to the ObjectMapper.
     */
    private void configureObjectMapper() {
        RuleSetSerializer serializer = new RuleSetSerializer();
        RuleSetDeserializer deserializer = new RuleSetDeserializer(matcherDefinitions, ruleDefinitions);

        SimpleModule module = new SimpleModule("RuleSet Serialization");
        module.addSerializer(RuleSet.class, serializer);
        module.addDeserializer(RuleSet.class, deserializer);

        objectMapper.registerModule(module);
    }

    @Bean
    public SchemaGenerator schemaGenerator() {
        return new SchemaGenerator(matcherDefinitions, ruleDefinitions, objectMapper);
    }
}
