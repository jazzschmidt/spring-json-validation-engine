package com.github.jazzschmidt.spring.jsonvalidation.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

/**
 * Servlet Filter that wraps the {@link ServletRequest} in {@link JsonContentHttpRequestWrapper} in order to provide the
 * requests content via the wrapper to the {@link ValidatorHandlerInterceptor}.
 */
@Component
public class JsonContentFilter implements Filter {

    /**
     * Wraps the request with the {@link JsonContentHttpRequestWrapper} if it is eligible for JSON validation.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param chain    Servlet filter chain
     * @throws IOException      if an I/O error occurs during this filter's processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (isRequestWithJsonBody(httpRequest)) {
            JsonContentHttpRequestWrapper requestWrapper = new JsonContentHttpRequestWrapper(httpRequest);
            chain.doFilter(requestWrapper, response);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Checks if the request has JSON content and its method is either one of POST, PUT or PATCH.
     *
     * @param request HTTP request
     * @return true if the request is eligible for JSON validation
     */
    private boolean isRequestWithJsonBody(HttpServletRequest request) {
        String method = request.getMethod();
        String contentType = request.getContentType();

        if (!Arrays.asList("POST", "PUT", "PATCH").contains(method)) {
            return false;
        }

        return contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}
