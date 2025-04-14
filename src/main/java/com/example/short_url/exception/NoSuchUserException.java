package com.example.short_url.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String message) {
        super(message);
    }
}
