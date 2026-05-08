package com.khureka.server.domain;

/**
 * 일정 상태.
 *
 * APPLICATION_OPEN    → 응모 접수 중
 * APPLICATION_CLOSED  → 응모 마감
 * LOTTERY_DONE        → 추첨 완료
 * FINISHED            → 행사 종료
 */
public enum ScheduleStatus {
    APPLICATION_OPEN,
    APPLICATION_CLOSED,
    LOTTERY_DONE,
    FINISHED
}
