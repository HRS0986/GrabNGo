package com.apigateway.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/forget-password",
            "/auth/verify",
            "/auth/reset-password"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        String method = String.valueOf(request.getMethod());

        if (path.contains("/product") && "GET".equalsIgnoreCase(method)) {
            return false;
        }

        if (path.contains("/categories") && "GET".equalsIgnoreCase(method)) {
            return false;
        }

        // Skip validation for other open API endpoints
        return openApiEndpoints.stream().noneMatch(path::contains);
    };
}
