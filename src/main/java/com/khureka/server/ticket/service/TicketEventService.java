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
import java.time.temporal.TemporalAdjusters;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketEventService {

    private final TicketEventRepository ticketEventRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final SeatZoneRepository seatZoneRepository;
    private final SeatRepository seatRepository;

    // ──────────────────────────────────────────
    // Step 1: 공연/경기 검색 & 상세 조회
    // ──────────────────────────────────────────

    public List<TicketEventResponse> getAllEvents() {
        return ticketEventRepository.findAll().stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    public List<TicketEventResponse> getEventsByCategory(EventCategory category) {
        return ticketEventRepository.findByCategory(category).stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    public List<TicketEventResponse> searchEvents(String keyword) {
        return ticketEventRepository.searchByKeyword(keyword).stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    public List<TicketEventResponse> searchEvents(EventCategory category, String keyword) {
        return ticketEventRepository.searchByCategoryAndKeyword(category, keyword).stream()
                .map(TicketEventResponse::from)
                .toList();
    }

    public TicketEventResponse getEvent(Long eventId) {
        TicketEvent event = ticketEventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        return TicketEventResponse.from(event);
    }

    // ──────────────────────────────────────────
    // 홈 화면 API: 내 근처 문화 & 이런 문화도 있어요
    // ──────────────────────────────────────────

    public EventHomeResponse getHomeEvents(Double userLat, Double userLon) {
        List<TicketEvent> allEvents = ticketEventRepository.findAll();

        List<EventSummaryResponse> summaries = allEvents.stream()
                .map(event -> toSummary(event, userLat, userLon))
                .toList();

        // 1. 내 근처 문화: 거리 순 상위 5개
        List<EventSummaryResponse> nearbyEvents;
        if (userLat != null && userLon != null) {
            nearbyEvents = summaries.stream()
                    .filter(s -> s.getDistance() != null)
                    .sorted(Comparator.comparing(EventSummaryResponse::getDistance))
                    .limit(5)
                    .toList();
        } else {
            nearbyEvents = summaries.stream().limit(5).toList();
        }

        // 2. 이런 문화도 있어요: 나머지 중 랜덤하게 최대 5개
        List<EventSummaryResponse> remaining = summaries.stream()
                .filter(s -> !nearbyEvents.contains(s))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(remaining);

        List<EventSummaryResponse> recommendedEvents = remaining.stream()
                .limit(5)
                .toList();

        return EventHomeResponse.builder()
                .nearbyEvents(nearbyEvents)
                .recommendedEvents(recommendedEvents)
                .build();
    }

    // ──────────────────────────────────────────
    // 맞춤형 추천 API: 날짜 필터 기반 (이미지 흐름)
    // ──────────────────────────────────────────

    /**
     * 필터링된 공연 목록 조회 (키워드 + 카테고리 + 날짜).
     */
    public List<EventSummaryResponse> getFilteredEvents(TimeFilter timeFilter, EventCategory category, String keyword) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now;
        LocalDateTime end = null;

        // 1. 날짜 범위 계산
        if (timeFilter != null) {
            switch (timeFilter) {
                case WEEKEND -> {
                    start = now.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY)).with(LocalTime.MIN);
                    end = now.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).with(LocalTime.MAX);
                }
                case NEXT_WEEK -> {
                    start = now.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY)).with(LocalTime.MIN);
                    end = start.plusDays(6).with(LocalTime.MAX);
                }
                case THIS_MONTH -> {
                    end = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
                }
                case TWO_MONTHS -> {
                    end = now.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
                }
                case ANYTIME -> {
                    end = now.plusYears(1);
                }
            }
        } else {
            end = now.plusYears(1); // 필터 없으면 무제한
        }

        final LocalDateTime finalStart = start;
        final LocalDateTime finalEnd = end;

        // 2. 카테고리 및 키워드 1차 필터링
        List<TicketEvent> events;
        if (category != null && keyword != null) {
            events = ticketEventRepository.searchByCategoryAndKeyword(category, keyword);
        } else if (category != null) {
            events = ticketEventRepository.findByCategory(category);
        } else if (keyword != null) {
            events = ticketEventRepository.searchByKeyword(keyword);
        } else {
            events = ticketEventRepository.findAll();
        }

        // 3. 날짜 범위로 2차 필터링 및 DTO 변환
        return events.stream()
                .filter(event -> hasScheduleInRange(event.getId(), finalStart, finalEnd))
                .map(event -> toSummary(event, null, null))
                .toList();
    }

    // ──────────────────────────────────────────
    // 내부 헬퍼 메소드
    // ──────────────────────────────────────────

    private EventSummaryResponse toSummary(TicketEvent event, Double userLat, Double userLon) {
        // 거리 계산
        Double distance = null;
        String distanceDisplay = null;
        if (userLat != null && userLon != null &&
                event.getDestinationLatitude() != null && event.getDestinationLongitude() != null) {
            distance = calculateDistance(userLat, userLon, event.getDestinationLatitude(), event.getDestinationLongitude());
            distanceDisplay = String.format("%.1fkm", distance);
        }

        // 날짜 및 가격 정보
        List<EventSchedule> schedules = eventScheduleRepository.findByEventIdOrderByStartTimeAsc(event.getId());
        String representativeDate = "";
        String dateRange = "";
        if (!schedules.isEmpty()) {
            LocalDateTime first = schedules.get(0).getStartTime();
            representativeDate = first.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            LocalDateTime last = schedules.get(schedules.size() - 1).getStartTime();
            dateRange = first.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + " - " + last.format(DateTimeFormatter.ofPattern("MM.dd"));
        }

        // 가격 범위
        List<Long> scheduleIds = schedules.stream().map(EventSchedule::getId).toList();
        List<SeatZone> zones = seatZoneRepository.findAll().stream()
                .filter(z -> scheduleIds.contains(z.getSchedule().getId()))
                .toList();
        Integer minPrice = zones.stream().map(SeatZone::getPrice).min(Integer::compare).orElse(null);
        Integer maxPrice = zones.stream().map(SeatZone::getPrice).max(Integer::compare).orElse(null);

        return EventSummaryResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .venueName(event.getVenueName())
                .dateRange(dateRange)
                .representativeDate(representativeDate)
                .category(event.getCategory())
                .thumbnailUrl(event.getThumbnailUrl())
                .distance(distance)
                .distanceDisplay(distanceDisplay)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
    }

    private boolean hasScheduleInRange(Long eventId, LocalDateTime start, LocalDateTime end) {
        List<EventSchedule> schedules = eventScheduleRepository.findByEventIdOrderByStartTimeAsc(eventId);
        return schedules.stream().anyMatch(s ->
                (s.getStartTime().isAfter(start) || s.getStartTime().isEqual(start)) &&
                (s.getStartTime().isBefore(end) || s.getStartTime().isEqual(end))
        );
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ──────────────────────────────────────────
    // Step 2, 4 로직
    // ──────────────────────────────────────────

    public List<EventScheduleResponse> getSchedules(Long eventId) {
        return eventScheduleRepository.findByEventIdOrderByStartTimeAsc(eventId).stream()
                .map(EventScheduleResponse::from)
                .toList();
    }

    public List<SeatZoneResponse> getSeatZones(Long scheduleId) {
        return seatZoneRepository.findByScheduleId(scheduleId).stream()
                .map(zone -> {
                    long available = seatRepository.countBySeatZoneIdAndStatus(zone.getId(), SeatStatus.AVAILABLE);
                    return SeatZoneResponse.from(zone, available);
                })
                .toList();
    }
}
