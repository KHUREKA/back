package com.khureka.server.domain;

/**
 * Mock 결제 상태.
 *
 * READY   → 결제 대기
 * SUCCESS → 결제 성공
 * FAILED  → 결제 실패
 */
public enum PaymentStatus {
    READY,
    SUCCESS,
    FAILED
}
