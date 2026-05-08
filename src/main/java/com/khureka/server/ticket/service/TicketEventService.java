package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.*;
import com.khureka.server.ticket.dto.*;
import com.khureka.server.ticket.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 티켓 이벤트 서비스.
 *
 * Step 1: 공연/경기 검색
 * Step 2: 일정 조회
 * Step 4: 좌석 구역 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketEventService {

    private final TicketEventRepository ticketEventRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final SeatZoneRepository seatZoneRepository;
    private final SeatRepository seatRepository;

    // ──────────────────────────────────────────
    // Step 1: 공연/경기 검색
    // ──────────────────────────────────────────

    /**
     * 전체 공연/경기 목록 조회.
     */
    public List<TicketEventResponse> getAllEvents() {
        return ticketEventRepository.findAll().stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    /**
     * 카테고리별 공연/경기 조회.
     */
    public List<TicketEventResponse> getEventsByCategory(EventCategory category) {
        return ticketEventRepository.findByCategory(category).stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    /**
     * 키워드 통합 검색.
     */
    public List<TicketEventResponse> searchEvents(String keyword) {
        return ticketEventRepository.searchByKeyword(keyword).stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    /**
     * 카테고리 + 키워드 복합 검색.
     */
    public List<TicketEventResponse> searchEvents(EventCategory category, String keyword) {
        return ticketEventRepository.searchByCategoryAndKeyword(category, keyword).stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    /**
     * 공연/경기 상세 조회.
     */
    public TicketEventResponse getEvent(Long eventId) {
        TicketEvent event = ticketEventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        return TicketEventResponse.from(event);
    }

    // ──────────────────────────────────────────
    // Step 2: 일정 조회
    // ──────────────────────────────────────────

    /**
     * 특정 공연의 전체 일정 목록 조회.
     */
    public List<EventScheduleResponse> getSchedules(Long eventId) {
        return eventScheduleRepository.findByEventIdOrderByStartTimeAsc(eventId).stream()
                .map(EventScheduleResponse::from)
                .toList();
    }

    /**
     * 특정 공연의 현재 응모 가능한 일정 조회.
     */
    public List<EventScheduleResponse> getOpenSchedules(Long eventId) {
        return eventScheduleRepository.findOpenSchedules(eventId, LocalDateTime.now()).stream()
                .map(EventScheduleResponse::from)
                .toList();
    }

    // ──────────────────────────────────────────
    // Step 4: 좌석 구역 조회 (잔여 좌석 수 포함)
    // ──────────────────────────────────────────

    /**
     * 특정 일정의 좌석 구역 목록 (잔여 좌석 수 포함).
     */
    public List<SeatZoneResponse> getSeatZones(Long scheduleId) {
        List<SeatZone> zones = seatZoneRepository.findByScheduleId(scheduleId);

        return zones.stream()
                .map(zone -> {
                    long available = seatRepository.countBySeatZoneIdAndStatus(
                            zone.getId(), SeatStatus.AVAILABLE);
                    return SeatZoneResponse.from(zone, available);
                })
                .toList();
    }
}
