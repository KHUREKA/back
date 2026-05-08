package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.*;
import com.khureka.server.ticket.dto.ApplicationRequest;
import com.khureka.server.ticket.dto.ApplicationResponse;
import com.khureka.server.ticket.dto.TicketResponse;
import com.khureka.server.auth.repository.UserRepository;
import com.khureka.server.ticket.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 응모 서비스.
 *
 * Step 3~5: 좌석 개수 선택 + 구역 선택 + 응모 완료
 * Step 11: 티켓 보관함 / 마이페이지 응모 내역
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketApplicationService {

    private final TicketApplicationRepository applicationRepository;
    private final ApplicationAssignedSeatRepository assignedSeatRepository;
    private final EventScheduleRepository scheduleRepository;
    private final SeatZoneRepository seatZoneRepository;
    private final UserRepository userRepository;

    // ──────────────────────────────────────────
    // Step 3~5: 응모 완료
    // ──────────────────────────────────────────

    /**
     * 응모를 접수한다.
     *
     * 검증 사항:
     * 1. 좌석 개수 1~4 범위
     * 2. autoAssign=false 일 때 priority1SeatZoneId 필수
     * 3. 일정 상태 = APPLICATION_OPEN
     * 4. 현재 시간이 응모 기간 내
     * 5. 동일 사용자 중복 응모 방지
     * 6. 선택한 구역이 해당 일정 소속인지
     */
    @Transactional
    public ApplicationResponse apply(Long userId, ApplicationRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 일정 조회
        EventSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 검증 1: 좌석 개수
        int seatCount = (request.getRequestedSeatCount() != null) ? request.getRequestedSeatCount() : 1;
        if (seatCount < 1 || seatCount > 4) {
            throw new BusinessException(ErrorCode.INVALID_SEAT_COUNT);
        }

        // 검증 2: 일정 상태
        if (schedule.getStatus() != ScheduleStatus.APPLICATION_OPEN) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_OPEN);
        }

        // 검증 3: 응모 기간
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(schedule.getApplicationOpenAt()) || now.isAfter(schedule.getApplicationCloseAt())) {
            throw new BusinessException(ErrorCode.APPLICATION_PERIOD_CLOSED);
        }

        // 검증 4: 중복 응모
        if (applicationRepository.existsByUserIdAndScheduleId(userId, schedule.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_APPLICATION);
        }

        boolean autoAssign = Boolean.TRUE.equals(request.getAutoAssign());

        // 검증 5: 수동 선택 시 1순위 필수
        if (!autoAssign && request.getPriority1SeatZoneId() == null) {
            throw new BusinessException(ErrorCode.PRIORITY_ZONE_REQUIRED);
        }

        // 구역 조회 및 검증 6: 해당 일정 소속 확인
        SeatZone priority1 = null;
        SeatZone priority2 = null;
        SeatZone priority3 = null;

        if (!autoAssign) {
            priority1 = findAndValidateZone(request.getPriority1SeatZoneId(), schedule.getId());

            if (request.getPriority2SeatZoneId() != null) {
                priority2 = findAndValidateZone(request.getPriority2SeatZoneId(), schedule.getId());
            }
            if (request.getPriority3SeatZoneId() != null) {
                priority3 = findAndValidateZone(request.getPriority3SeatZoneId(), schedule.getId());
            }
        }

        // 응모 생성
        TicketApplication application = TicketApplication.builder()
                .user(user)
                .schedule(schedule)
                .requestedSeatCount(seatCount)
                .autoAssign(autoAssign)
                .priority1SeatZone(priority1)
                .priority2SeatZone(priority2)
                .priority3SeatZone(priority3)
                .autoPaymentAgreed(request.getAutoPaymentAgreed())
                .build();

        applicationRepository.save(application);

        return ApplicationResponse.from(application);
    }

    /**
     * 구역 ID로 조회 + 해당 일정 소속 검증.
     */
    private SeatZone findAndValidateZone(Long zoneId, Long scheduleId) {
        SeatZone zone = seatZoneRepository.findById(zoneId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_ZONE_NOT_FOUND));

        if (!zone.getSchedule().getId().equals(scheduleId)) {
            throw new BusinessException(ErrorCode.ZONE_NOT_IN_SCHEDULE);
        }

        return zone;
    }

    // ──────────────────────────────────────────
    // Step 11: 마이페이지 응모 내역 조회
    // ──────────────────────────────────────────

    /**
     * 사용자의 전체 응모 내역 조회.
     */
    public List<ApplicationResponse> getMyApplications(Long userId) {
        return applicationRepository.findApplicationHistory(userId).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    // ──────────────────────────────────────────
    // Step 11: 티켓 보관함 조회
    // ──────────────────────────────────────────

    /**
     * 사용자의 발급된 티켓 목록 조회.
     */
    public List<TicketResponse> getMyTickets(Long userId) {
        List<TicketApplication> tickets = applicationRepository.findTicketsForWallet(userId);

        return tickets.stream()
                .map(app -> {
                    List<ApplicationAssignedSeat> assignedSeats =
                            assignedSeatRepository.findByApplicationIdWithSeat(app.getId());
                    return TicketResponse.from(app, assignedSeats);
                })
                .toList();
    }
}
