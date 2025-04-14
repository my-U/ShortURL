package com.example.short_url.util;

import com.example.short_url.exception.*;
import com.example.short_url.util.enums.ErrorCode;
import com.example.short_url.util.enums.ErrorResponse;
import com.example.short_url.util.enums.SuccessCode;
import com.example.short_url.util.enums.SuccessResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityExistsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class ResponseUtil {

    // 예외 타입과 ErrorCode 매핑
    private static final Map<Class<? extends Exception>, ErrorCode> EXCEPTION_MAP = new HashMap<>();

    static {
        EXCEPTION_MAP.put(IllegalArgumentException.class, ErrorCode.BAD_REQUEST_ERROR);
        EXCEPTION_MAP.put(IllegalStateException.class, ErrorCode.BAD_REQUEST_ERROR);
        EXCEPTION_MAP.put(NoSuchElementException.class, ErrorCode.NOT_FOUND_ERROR);
        EXCEPTION_MAP.put(NullPointerException.class, ErrorCode.NULL_POINT_ERROR);
        EXCEPTION_MAP.put(MissingRequestHeaderException.class, ErrorCode.NOT_VALID_HEADER_ERROR);
        EXCEPTION_MAP.put(HttpMessageNotReadableException.class, ErrorCode.REQUEST_BODY_MISSING_ERROR);
        EXCEPTION_MAP.put(MissingServletRequestParameterException.class, ErrorCode.MISSING_REQUEST_PARAMETER_ERROR);
        EXCEPTION_MAP.put(HttpClientErrorException.BadRequest.class, ErrorCode.BAD_REQUEST_ERROR);
        EXCEPTION_MAP.put(NoHandlerFoundException.class, ErrorCode.NOT_FOUND_ERROR);
        EXCEPTION_MAP.put(NullPointerException.class, ErrorCode.NULL_POINT_ERROR);
        EXCEPTION_MAP.put(IOException.class, ErrorCode.IO_ERROR);
        EXCEPTION_MAP.put(EntityExistsException.class, ErrorCode.DUPLICATED_ERROR);
        EXCEPTION_MAP.put(JsonParseException.class, ErrorCode.JSON_PARSE_ERROR);
        EXCEPTION_MAP.put(JsonProcessingException.class, ErrorCode.JACKSON_PROCESS_ERROR);
        EXCEPTION_MAP.put(DuplicateKeyException.class, ErrorCode.DUPLICATED_ERROR);
        EXCEPTION_MAP.put(NoSuchElementException.class, ErrorCode.NO_SUCH_ELEMENT);
        EXCEPTION_MAP.put(MethodArgumentNotValidException.class, ErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION);
        EXCEPTION_MAP.put(AccessDeniedException.class, ErrorCode.FORBIDDEN_ERROR);
        EXCEPTION_MAP.put(AuthenticationException.class, ErrorCode.UNAUTHORIZED);
        EXCEPTION_MAP.put(UnauthorizedException.class, ErrorCode.UNAUTHORIZED);
        EXCEPTION_MAP.put(JwtTokenExpiredException.class, ErrorCode.JWT_TOKEN_IS_EXPIRED);
        EXCEPTION_MAP.put(JwtTokenIsNotValidException.class, ErrorCode.JWT_TOKEN_IS_NOT_VALID_EXCEPTION);
        EXCEPTION_MAP.put(InvalidPasswordException.class, ErrorCode.INVALID_PASSWORD_EXCEPTION);
        EXCEPTION_MAP.put(NoSuchUserException.class, ErrorCode.NO_SUCH_USER_EXCEPTION);
        EXCEPTION_MAP.put(MissingRequestParameterException.class, ErrorCode.MISSING_REQUEST_PARAMETER_ERROR);
    }

    // 인스턴스화 방지
    private ResponseUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static <T> ResponseEntity<SuccessResponse<T>> createSuccessResponse(SuccessCode successCode, T data) {
        SuccessResponse<T> response = new SuccessResponse<>(successCode, data);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static ResponseEntity<SuccessResponse<Void>> createSuccessResponse(SuccessCode successCode) {
        SuccessResponse<Void> response = new SuccessResponse<>(successCode);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode errorCode = findErrorCode(e);
        return createErrorResponse(errorCode, e.getMessage());
    }

    public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, String errorDescription) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorDescription);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    private static ErrorCode findErrorCode(Exception e) {
        // EXCEPTION_MAP에서 예외 클래스 찾기
        ErrorCode errorCode = EXCEPTION_MAP.get(e.getClass());

        // 정확히 일치하는 클래스가 없으면, 부모 클래스를 탐색해서 찾기
        if (errorCode == null) {
            Class<?> exceptionClass = e.getClass();
            while (exceptionClass != null && exceptionClass != Object.class) {
                errorCode = EXCEPTION_MAP.get(exceptionClass);
                if (errorCode != null) {
                    break;
                }
                exceptionClass = exceptionClass.getSuperclass(); // 🔹 부모 클래스로 이동
            }
        }

        // 못 찾으면 기본값(500 Internal Server Error) 반환
        return errorCode != null ? errorCode : ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
