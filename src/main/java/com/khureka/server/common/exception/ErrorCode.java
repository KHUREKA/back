package com.khureka.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 프로젝트 전체 에러 코드를 중앙 관리하는 enum.
 *
 * 코드 형식:
 * - COMMON-400
 * - AUTH-401-001
 * - USER-404-001
 * - POST-404-001
 *
 * 작성 규칙:
 * - 공통 에러는 COMMON-{HTTP_STATUS}
 * - 도메인 에러는 {DOMAIN}-{HTTP_STATUS}-{NUMBER}
 * - 같은 도메인 안에서 같은 상태 코드가 여러 개면 001, 002 순서로 증가
 */
@Getter
public enum ErrorCode {

    // ===== Common =====
    INVALID_REQUEST("COMMON-400", 400, "잘못된 요청입니다."),
    INVALID_TYPE("COMMON-400-001", 400, "요청 값의 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("COMMON-405", 405, "지원하지 않는 HTTP 메서드입니다."),
    UNAUTHORIZED("COMMON-401", 401, "인증이 필요합니다."),
    FORBIDDEN("COMMON-403", 403, "권한이 없습니다."),
    NOT_FOUND("COMMON-404", 404, "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT("COMMON-409", 409, "리소스 충돌이 발생했습니다."),
    INTERNAL_ERROR("COMMON-500", 500, "서버 내부 오류입니다."),

    /*
     * 공통 에러 타입 정의
     *
     * | 타입 | 설명 | HTTP 상태 코드 |
     * |------|------|----------------|
     * | Bad Request | 잘못된 요청 | 400 |
     * | Unauthorized | 인증되지 않은 요청 | 401 |
     * | Forbidden | 권한이 없는 요청 | 403 |
     * | Not Found | 요청한 리소스를 찾을 수 없음 | 404 |
     * | Conflict | 리소스 충돌 발생 | 409 |
     * | Method Not Allowed | 지원하지 않는 HTTP 메서드 | 405 |
     * | Internal Server Error | 서버 내부 오류 | 500 |
     */

    // ===== Auth =====
    AUTH_INVALID_CREDENTIAL("AUTH-401-001", 401, "이메일 또는 비밀번호가 올바르지 않습니다."),
    AUTH_EXPIRED_TOKEN("AUTH-401-002", 401, "만료된 토큰입니다."),
    AUTH_INVALID_TOKEN("AUTH-401-003", 401, "유효하지 않은 토큰입니다."),

    // ===== User =====
    USER_NOT_FOUND("USER-404-001", 404, "사용자를 찾을 수 없습니다."),
    USER_DUPLICATE_EMAIL("USER-409-001", 409, "이미 사용 중인 이메일입니다."),
    USER_INVALID_PASSWORD("USER-400-001", 400, "비밀번호가 일치하지 않습니다."),

    // ===== Post =====
    POST_NOT_FOUND("POST-404-001", 404, "게시글을 찾을 수 없습니다."),

    // ===== Ticket =====
    EVENT_NOT_FOUND("TICKET-404-001", 404, "공연/경기를 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND("TICKET-404-002", 404, "일정을 찾을 수 없습니다."),
    SEAT_ZONE_NOT_FOUND("TICKET-404-003", 404, "좌석 구역을 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND("TICKET-404-004", 404, "응모 내역을 찾을 수 없습니다."),

    APPLICATION_PERIOD_CLOSED("TICKET-400-001", 400, "응모 기간이 아닙니다."),
    INVALID_SEAT_COUNT("TICKET-400-002", 400, "좌석 개수는 1~4장만 선택 가능합니다."),
    PRIORITY_ZONE_REQUIRED("TICKET-400-003", 400, "수동 선택 시 1순위 좌석 구역은 필수입니다."),
    ZONE_NOT_IN_SCHEDULE("TICKET-400-004", 400, "선택한 구역이 해당 일정에 속하지 않습니다."),
    SCHEDULE_NOT_OPEN("TICKET-400-005", 400, "응모 접수가 열려있지 않은 일정입니다."),

    DUPLICATE_APPLICATION("TICKET-409-001", 409, "이미 해당 일정에 응모하셨습니다."),

    LOTTERY_ALREADY_DONE("TICKET-400-006", 400, "이미 추첨이 완료된 일정입니다."),
    ALREADY_CANCELLED("TICKET-400-007", 400, "이미 취소된 응모 내역입니다."),
    CANNOT_CANCEL_AFTER_LOTTERY("TICKET-400-008", 400, "추첨이 진행된 이후에는 취소할 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;

    ErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus toHttpStatus() {
        return HttpStatus.valueOf(httpStatus);
    }
}