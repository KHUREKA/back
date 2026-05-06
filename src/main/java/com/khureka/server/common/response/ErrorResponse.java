package com.khureka.server.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khureka.server.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 에러 상세 응답 객체.
 *
 * 이 객체는 보통 API 최상위 응답의 error 필드에 들어간다.
 *
 * 예:
 * {
 *   "result": "ERROR",
 *   "error": {
 *     "code": "USER-404-001",
 *     "message": "사용자를 찾을 수 없습니다.",
 *     "timestamp": "2026-05-05T18:10:22.123+09:00",
 *     "path": "/api/v1/users/1"
 *   }
 * }
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String code;
    private final String message;
    private final OffsetDateTime timestamp;
    private final String path;
    private final List<FieldError> errors;

    /**
     * code/message만으로 단순 에러 응답 생성.
     * path가 없기 때문에 주로 테스트 또는 특수 상황에서만 사용한다.
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    /**
     * ErrorCode의 기본 메시지로 에러 응답 생성.
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    /**
     * ErrorCode의 기본 메시지 + 요청 path로 에러 응답 생성.
     */
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build();
    }

    /**
     * ErrorCode의 기본 메시지 대신 상세 메시지 사용.
     */
    public static ErrorResponse of(ErrorCode errorCode, String detail, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(detail)
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build();
    }

    /**
     * 필드 검증 에러 목록을 포함한 에러 응답 생성.
     */
    public static ErrorResponse of(
            ErrorCode errorCode,
            String detail,
            String path,
            List<FieldError> errors
    ) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(detail)
                .timestamp(OffsetDateTime.now())
                .path(path)
                .errors(errors)
                .build();
    }

    /**
     * @Valid 검증 실패 시 필드별 에러 정보를 담는 객체.
     *
     * 예:
     * {
     *   "field": "email",
     *   "reason": "이메일 형식이 올바르지 않습니다."
     * }
     */
    @Getter
    @Builder
    public static class FieldError {

        private final String field;
        private final String reason;
    }
}