package com.khureka.server.ticket.controller;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.domain.EventCategory;
import com.khureka.server.ticket.dto.EventScheduleResponse;
import com.khureka.server.ticket.dto.SeatZoneResponse;
import com.khureka.server.ticket.dto.TicketEventResponse;
import com.khureka.server.ticket.service.TicketEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공연/경기 조회 API.
 *
 * Step 1: 공연/경기 검색
 * Step 2: 일정 조회
 * Step 4: 좌석 구역 조회
 */
@Tag(name = "Ticket Event", description = "공연/경기 조회 API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class TicketEventController {

    private final TicketEventService ticketEventService;

    @Operation(summary = "공연/경기 목록 조회", description = "전체 목록 또는 카테고리/키워드로 검색")
    @GetMapping
    public ApiResponse<List<TicketEventResponse>> getEvents(
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String keyword) {

        List<TicketEventResponse> result;

        if (category != null && keyword != null) {
            result = ticketEventService.searchEvents(category, keyword);
        } else if (keyword != null) {
            result = ticketEventService.searchEvents(keyword);
        } else if (category != null) {
            result = ticketEventService.getEventsByCategory(category);
        } else {
            result = ticketEventService.getAllEvents();
        }

        return ApiResponse.success(result);
    }

    @Operation(summary = "공연/경기 상세 조회")
    @GetMapping("/{eventId}")
    public ApiResponse<TicketEventResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success(ticketEventService.getEvent(eventId));
    }

    @Operation(summary = "일정 목록 조회", description = "특정 공연의 전체 일정")
    @GetMapping("/{eventId}/schedules")
    public ApiResponse<List<EventScheduleResponse>> getSchedules(@PathVariable Long eventId) {
        return ApiResponse.success(ticketEventService.getSchedules(eventId));
    }

    @Operation(summary = "응모 가능 일정 조회", description = "현재 응모 접수 중인 일정만")
    @GetMapping("/{eventId}/schedules/open")
    public ApiResponse<List<EventScheduleResponse>> getOpenSchedules(@PathVariable Long eventId) {
        return ApiResponse.success(ticketEventService.getOpenSchedules(eventId));
    }

    @Operation(summary = "좌석 구역 목록 조회", description = "특정 일정의 좌석 구역 + 잔여 좌석 수")
    @GetMapping("/schedules/{scheduleId}/zones")
    public ApiResponse<List<SeatZoneResponse>> getSeatZones(@PathVariable Long scheduleId) {
        return ApiResponse.success(ticketEventService.getSeatZones(scheduleId));
    }
}
