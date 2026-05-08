package com.khureka.server.domain;

/**
 * 응모 상태.
 *
 * APPLIED       → 응모 완료 (추첨 대기)
 * LOSE          → 미당첨
 * TICKET_ISSUED → 당첨 + Mock 결제 + 티켓 발급 완료
 */
public enum ApplicationStatus {
    APPLIED,
    LOSE,
    TICKET_ISSUED
}
