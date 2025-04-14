package com.example.short_url.util.enums;

import com.example.short_url.util.ResponseUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;                 // 에러 상태 코드
    private String customStatus;        // 에러 구분 코드
    private String message;             // 에러 메시지
    private String errorDescription;    // 에러 이유

    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화 적용
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화 적용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;    // 에러 발생 시간


    /**
     * ErrorResponse 생성자-1
     *
     * @param code ErrorCode
     */
    @Builder
    protected ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.customStatus = code.getCustomStatus();
        this.timestamp = LocalDateTime.now();
    }

    /**
     * ErrorResponse 생성자-2x
     *
     * @param code   ErrorCode
     * @param errorDescription String
     */
    @Builder
    public ErrorResponse(final ErrorCode code, final String errorDescription) {
        this.status = code.getStatus();
        this.customStatus = code.getCustomStatus();
        this.message = code.getMessage();
        this.errorDescription = errorDescription;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Global Exception 전송 타입-1
     *
     * @param code ErrorCode
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    /**
     * Global Exception 전송 타입-2
     *
     * @param code   ErrorCode
     * @param reason String
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }

    public static void sendErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        ResponseEntity<ErrorResponse> errorResponse = ResponseUtil.handleException(e);

        response.setStatus(errorResponse.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse.getBody()));
    }
}
