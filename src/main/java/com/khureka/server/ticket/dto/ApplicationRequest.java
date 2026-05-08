package com.khureka.server.ticket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 응모 요청 DTO.
 *
 * 수동 선택 예시:
 * {
 *   "scheduleId": 10,
 *   "requestedSeatCount": 2,
 *   "autoAssign": false,
 *   "priority1SeatZoneId": 101,
 *   "priority2SeatZoneId": 102,
 *   "priority3SeatZoneId": 103,
 *   "autoPaymentAgreed": true
 * }
 *
 * 자동 선택 예시:
 * {
 *   "scheduleId": 10,
 *   "requestedSeatCount": 2,
 *   "autoAssign": true,
 *   "autoPaymentAgreed": true
 * }
 */
@Getter
@NoArgsConstructor
public class ApplicationRequest {

    private Long scheduleId;
    private Integer requestedSeatCount;
    private Boolean autoAssign;
    private Long priority1SeatZoneId;
    private Long priority2SeatZoneId;
    private Long priority3SeatZoneId;
    private Boolean autoPaymentAgreed;
}
