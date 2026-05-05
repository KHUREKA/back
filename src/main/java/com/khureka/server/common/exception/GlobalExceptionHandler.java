package com.khureka.server.common.exception;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * 전역 예외 처리기.
 *
 * Controller 또는 Service 계층에서 발생한 예외를 한 곳에서 처리하고,
 * ApiResponse + ErrorResponse 형식으로 통일된 에러 응답을 반환한다.
 *
 * 주의:
 * - Controller마다 try-catch를 작성하지 않는다.
 * - Service에서 ResponseEntity를 직접 만들지 않는다.
 * - 예상하지 못한 Exception은 반드시 log.error로 스택 트레이스를 남긴다.
 * - 클라이언트에게 500 내부 상세 메시지를 노출하지 않는다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 우리가 직접 정의한 비즈니스 예외 처리.
     *
     * 예:
     * throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("[BusinessException] code={}, message={}, path={}",
                errorCode.getCode(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                errorCode,
                                e.getMessage(),
                                request.getRequestURI()
                        )
                ));
    }

    /**
     * @Valid 검증 실패 처리.
     *
     * 예:
     * - 이메일 형식 오류
     * - 비밀번호 길이 부족
     * - 필수값 누락
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.FieldError.builder()
                        .field(fieldError.getField())
                        .reason(fieldError.getDefaultMessage())
                        .build()
                )
                .toList();

        String message = fieldErrors.isEmpty()
                ? ErrorCode.INVALID_REQUEST.getMessage()
                : fieldErrors.get(0).getReason();

        log.warn("[ValidationException] message={}, path={}",
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.INVALID_REQUEST,
                                message,
                                request.getRequestURI(),
                                fieldErrors
                        )
                ));
    }

    /**
     * 요청 파라미터 또는 PathVariable 타입 불일치 처리.
     *
     * 예:
     * /api/v1/users/abc
     * 그런데 Controller에서는 @PathVariable Long id를 기대하는 경우.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        String message = String.format(
                "'%s' 파라미터의 값 '%s'이 올바르지 않습니다.",
                e.getName(),
                e.getValue()
        );

        log.warn("[TypeMismatchException] message={}, path={}",
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_TYPE.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.INVALID_TYPE,
                                message,
                                request.getRequestURI()
                        )
                ));
    }

    /**
     * 지원하지 않는 HTTP 메서드 요청 처리.
     *
     * 예:
     * GET만 지원하는 API에 POST 요청을 보낸 경우.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        String message = e.getMethod() + " 메서드는 지원하지 않습니다.";

        log.warn("[MethodNotAllowedException] message={}, path={}",
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.METHOD_NOT_ALLOWED,
                                message,
                                request.getRequestURI()
                        )
                ));
    }

    /**
     * 요청 Body 파싱 실패 처리.
     *
     * 예:
     * - JSON 문법 오류
     * - 잘못된 Content-Type
     * - enum 변환 실패
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        String message = "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.";

        log.warn("[HttpMessageNotReadableException] message={}, path={}",
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.INVALID_REQUEST,
                                message,
                                request.getRequestURI()
                        )
                ));
    }

    /**
     * 인증 실패 처리.
     *
     * 현재 AuthService에서 AuthenticationManagerBuilder를 통해 인증할 때
     * 이메일 또는 비밀번호가 틀리면 AuthenticationException 계열 예외가 발생할 수 있다.
     *
     * 단, JWT 필터 단계에서 발생하는 401은 Security 전용 EntryPoint에서 처리하는 것이 더 적절하다.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException e,
            HttpServletRequest request
    ) {
        log.warn("[AuthenticationException] message={}, path={}",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.AUTH_INVALID_CREDENTIAL.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.AUTH_INVALID_CREDENTIAL,
                                ErrorCode.AUTH_INVALID_CREDENTIAL.getMessage(),
                                request.getRequestURI()
                        )
                ));
    }

    /**
     * 권한 없음 처리.
     *
     * @PreAuthorize 등 Controller 이후 단계에서 AccessDeniedException이 발생하는 경우 처리한다.
     *
     * 단, Spring Security Filter 단계의 403은 Security 전용 AccessDeniedHandler에서 처리하는 것이 더 적절하다.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException e,
            HttpServletRequest request
    ) {
        log.warn("[AccessDeniedException] message={}, path={}",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.FORBIDDEN,
                                ErrorCode.FORBIDDEN.getMessage(),
                                request.getRequestURI()
                        )
                ));
    }

    /**
     * 예상하지 못한 모든 예외 처리.
     *
     * 반드시 마지막에 둔다.
     * 서버 로그에는 스택 트레이스를 남기고,
     * 클라이언트에게는 내부 상세 메시지를 노출하지 않는다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("[UnhandledException] path={}", request.getRequestURI(), e);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.toHttpStatus())
                .body(ApiResponse.error(
                        ErrorResponse.of(
                                ErrorCode.INTERNAL_ERROR,
                                ErrorCode.INTERNAL_ERROR.getMessage(),
                                request.getRequestURI()
                        )
                ));
    }
}