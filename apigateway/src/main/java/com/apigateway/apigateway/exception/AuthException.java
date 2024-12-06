package com.apigateway.apigateway.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}

