package com.example.short_url.exception;

import com.example.short_url.util.ResponseUtil;
import com.example.short_url.util.enums.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice // @ControllerAdvice + @ResponseBody (JSON 응답 반환), 모든 컨트롤러에서 발생하는 예외를 JSON 형태로 반환.
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthenticationException.class,
            UnauthorizedException.class,
            IllegalArgumentException.class,
            IllegalStateException.class,
            NoSuchElementException.class,
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            HttpClientErrorException.BadRequest.class,
            NoHandlerFoundException.class,
            NullPointerException.class,
            IOException.class,
            EntityExistsException.class,
            JsonParseException.class,
            JsonProcessingException.class,
            DuplicateKeyException.class,
            NoSuchElementException.class,
            MethodArgumentNotValidException.class,
            MissingRequestParameterException.class,
            JwtTokenExpiredException.class,
            JwtTokenIsNotValidException.class,
            InvalidPasswordException.class,
            NoSuchUserException.class
    })
    protected ResponseEntity<ErrorResponse> handleSpecificExceptions(Exception ex) {
        log.error("Exception caught: {}", ex.getClass().getSimpleName(), ex);
        return ResponseUtil.handleException(ex);
    }
}