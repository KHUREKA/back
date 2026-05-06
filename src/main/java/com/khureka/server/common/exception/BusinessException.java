package com.khureka.server.common.exception;

import lombok.Getter;

/**
 * 비즈니스 예외의 최상위 예외 클래스.
 *
 * Service 계층에서 비즈니스 규칙 위반, 리소스 없음, 중복 요청 등의 상황이 발생하면
 * RuntimeException을 직접 던지지 않고 BusinessException 또는 이를 상속한 구체 예외를 던진다.
 *
 * 사용 예시:
 * throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
 *
 * 상세 메시지가 필요한 경우:
 * throw new BusinessException(
 *     ErrorCode.USER_NOT_FOUND,
 *     "ID 1에 해당하는 사용자를 찾을 수 없습니다."
 * );
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode의 기본 메시지를 사용하는 생성자.
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode의 기본 메시지 대신 상세 메시지를 사용하는 생성자.
     */
    public BusinessException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }

    /**
     * 원인 예외를 함께 전달하는 생성자.
     *
     * 외부 API 호출 실패, 파싱 실패 등 원본 예외를 보존해야 할 때 사용한다.
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 상세 메시지와 원인 예외를 함께 전달하는 생성자.
     */
    public BusinessException(ErrorCode errorCode, String detail, Throwable cause) {
        super(detail, cause);
        this.errorCode = errorCode;
    }
}