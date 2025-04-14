package com.example.short_url.exception;

public class JwtTokenIsNotValidException extends RuntimeException {
    public JwtTokenIsNotValidException(String message) {
        super(message);
    }
}
