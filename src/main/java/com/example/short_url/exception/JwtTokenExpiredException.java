package com.example.short_url.exception;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}