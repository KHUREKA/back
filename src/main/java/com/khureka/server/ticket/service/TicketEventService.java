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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    // ──────────────────────────────────────────
    // 홈 화면 API: 내 근처 문화 & 이런 문화도 있어요
    // ──────────────────────────────────────────

    /**
     * 홈 화면에 필요한 공연 목록을 조회한다.
     *
     * @param userLat 사용자 위도 (null 가능)
     * @param userLon 사용자 경도 (null 가능)
     */
    public EventHomeResponse getHomeEvents(Double userLat, Double userLon) {
        List<TicketEvent> allEvents = ticketEventRepository.findAll();

        List<EventSummaryResponse> summaries = allEvents.stream()
                .map(event -> toSummary(event, userLat, userLon))
                .toList();

        // 1. 내 근처 문화: 거리 순 상위 5개 (사용자 위치가 있을 때만 거리순 정렬)
        List<EventSummaryResponse> nearbyEvents;
        if (userLat != null && userLon != null) {
            nearbyEvents = summaries.stream()
                    .sorted(Comparator.comparing(EventSummaryResponse::getDistance))
                    .limit(5)
                    .toList();
        } else {
            // 위치 정보 없으면 그냥 상위 5개
            nearbyEvents = summaries.stream().limit(5).toList();
        }

        // 2. 이런 문화도 있어요: 나머지 (또는 전체 중 랜덤/최신순 등)
        // 여기서는 간단히 전체 목록을 반환하거나, 내 근처를 제외한 목록을 반환
        List<EventSummaryResponse> recommendedEvents = summaries.stream()
                .filter(s -> !nearbyEvents.contains(s))
                .toList();

        return EventHomeResponse.builder()
                .nearbyEvents(nearbyEvents)
                .recommendedEvents(recommendedEvents)
                .build();
    }

    private EventSummaryResponse toSummary(TicketEvent event, Double userLat, Double userLon) {
        // 거리 계산
        Double distance = null;
        String distanceDisplay = null;

        if (userLat != null && userLon != null &&
                event.getDestinationLatitude() != null && event.getDestinationLongitude() != null) {
            distance = calculateDistance(userLat, userLon,
                    event.getDestinationLatitude(), event.getDestinationLongitude());
            distanceDisplay = String.format("%.1fkm", distance);
        }

        // 날짜 범위 계산
        List<EventSchedule> schedules = eventScheduleRepository.findByEventIdOrderByStartTimeAsc(event.getId());
        String dateRange = "";
        if (!schedules.isEmpty()) {
            LocalDateTime start = schedules.get(0).getStartTime();
            LocalDateTime end = schedules.get(schedules.size() - 1).getStartTime(); // 종료 시간으로 할 수도 있지만 보통 시작일~마지막날시작일

            DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("MM.dd");

            dateRange = start.format(fullFormatter) + " - " + end.format(shortFormatter);
        }

        return EventSummaryResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .venueName(event.getVenueName())
                .dateRange(dateRange)
                .category(event.getCategory())
                .thumbnailUrl(event.getThumbnailUrl())
                .distance(distance)
                .distanceDisplay(distanceDisplay)
                .build();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반경 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
