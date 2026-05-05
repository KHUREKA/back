package com.khureka.server.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khureka.server.common.exception.ErrorCode;
import lombok.Getter;

import java.util.List;

/**
 * API 최상위 공통 응답 래퍼.
 *
 * 성공 응답:
 * {
 *   "result": "SUCCESS",
 *   "data": { ... }
 * }
 *
 * 에러 응답:
 * {
 *   "result": "ERROR",
 *   "error": {
 *     "code": "USER-404-001",
 *     "message": "사용자를 찾을 수 없습니다.",
 *     "timestamp": "2026-05-05T18:10:22.123+09:00",
 *     "path": "/api/v1/users/1"
 *   }
 * }
 *
 * 사용 규칙:
 * - Controller의 성공 응답은 ApiResponse.success(...)를 사용한다.
 * - 예외 상황은 Service에서 예외를 던지고 GlobalExceptionHandler에서 ApiResponse.error(...)로 변환한다.
 * - Controller나 Service에서 직접 에러 응답을 만들지 않는 것을 원칙으로 한다.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final ResultType result;
    private final T data;
    private final ErrorResponse error;

    private ApiResponse(ResultType result, T data, ErrorResponse error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    /**
     * 데이터가 없는 성공 응답.
     *
     * 예:
     * {
     *   "result": "SUCCESS"
     * }
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null);
    }

    /**
     * 데이터가 있는 성공 응답.
     *
     * 예:
     * {
     *   "result": "SUCCESS",
     *   "data": { ... }
     * }
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    /**
     * ErrorResponse를 직접 받아 에러 응답 생성.
     * GlobalExceptionHandler에서 가장 많이 사용한다.
     */
    public static ApiResponse<Void> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(ResultType.ERROR, null, errorResponse);
    }

    /**
     * code/message만으로 단순 에러 응답 생성.
     * 테스트 또는 예외 외 특수 상황에서만 사용한다.
     */
    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(
                ResultType.ERROR,
                null,
                ErrorResponse.of(code, message)
        );
    }

    /**
     * ErrorCode 기본 메시지로 에러 응답 생성.
     */
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                ResultType.ERROR,
                null,
                ErrorResponse.of(errorCode)
        );
    }

    /**
     * ErrorCode 기본 메시지 + 요청 path로 에러 응답 생성.
     */
    public static ApiResponse<Void> error(ErrorCode errorCode, String path) {
        return new ApiResponse<>(
                ResultType.ERROR,
                null,
                ErrorResponse.of(errorCode, path)
        );
    }

    /**
     * ErrorCode + 상세 메시지 + 요청 path로 에러 응답 생성.
     */
    public static ApiResponse<Void> error(ErrorCode errorCode, String detail, String path) {
        return new ApiResponse<>(
                ResultType.ERROR,
                null,
                ErrorResponse.of(errorCode, detail, path)
        );
    }

    /**
     * ErrorCode + 상세 메시지 + 요청 path + 필드 검증 에러 목록으로 에러 응답 생성.
     */
    public static ApiResponse<Void> error(
            ErrorCode errorCode,
            String detail,
            String path,
            List<ErrorResponse.FieldError> errors
    ) {
        return new ApiResponse<>(
                ResultType.ERROR,
                null,
                ErrorResponse.of(errorCode, detail, path, errors)
        );
    }

    public enum ResultType {
        SUCCESS,
        ERROR
    }
}