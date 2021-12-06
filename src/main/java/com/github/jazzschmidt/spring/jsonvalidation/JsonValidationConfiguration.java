package com.github.jazzschmidt.spring.jsonvalidation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("jsonvalidation")
@JsonValidationComponents
public class JsonValidationConfiguration {

    /**
     * Whether or not the REST endpoint is enabled
     */
    private boolean enableEndpoint = true;

    /**
     * Path of the REST endpoint
     */
    private String endpoint = "/jsonvalidation";

    public boolean isEnableEndpoint() {
        return enableEndpoint;
    }

    public void setEnableEndpoint(boolean enableEndpoint) {
        this.enableEndpoint = enableEndpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
