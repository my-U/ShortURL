package com.example.short_url.exception;

public class MissingRequestParameterException extends RuntimeException {
    public MissingRequestParameterException(String message) {
        super(message);
    }
}
