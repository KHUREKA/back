package com.khureka.server.ticket.controller;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.domain.EventCategory;
import com.khureka.server.domain.TimeFilter;
import com.khureka.server.ticket.dto.*;
import com.khureka.server.common.s3.S3Service;
import com.khureka.server.ticket.service.TicketEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Ticket Event", description = "공연/경기 관련 API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class TicketEventController {

    private final TicketEventService ticketEventService;
    private final S3Service s3Service;

    @Operation(summary = "공연/경기 이미지 업로드 (관리자)", description = "S3에 이미지를 업로드하고 URL을 반환합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadImage(@RequestPart("file") MultipartFile file) {
        String url = s3Service.upload(file, "events");
        return ApiResponse.success(url);
    }

    @Operation(summary = "홈 화면 정보 조회", description = "내 주변 문화(상위 5개) 및 이런 문화도 있어요 추천")
    @GetMapping("/home")
    public ApiResponse<EventHomeResponse> getHomeEvents(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        return ApiResponse.success(ticketEventService.getHomeEvents(lat, lon));
    }

    @Operation(summary = "[STEP 1] 맞춤형 공연 추천 조회 (응모 플로우)", description = "날짜 필터, 카테고리, 키워드 기반으로 공연 목록을 필터링하여 조회합니다.")
    @GetMapping("/recommend")
    public ApiResponse<List<EventSummaryResponse>> getFilteredEvents(
            @RequestParam(required = false) TimeFilter timeFilter,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String keyword) {

        return ApiResponse.success(ticketEventService.getFilteredEvents(timeFilter, category, keyword));
    }

    @Operation(summary = "공연/경기 상세 조회")
    @GetMapping("/{eventId}")
    public ApiResponse<TicketEventResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success(ticketEventService.getEvent(eventId));
    }

    @Operation(summary = "[STEP 1.5] 공연/경기의 일정(회차) 목록 조회", description = "특정 공연의 날짜/시간 목록을 조회하여 scheduleId를 얻습니다.")
    @GetMapping("/{eventId}/schedules")
    public ApiResponse<List<EventScheduleResponse>> getSchedules(@PathVariable Long eventId) {
        return ApiResponse.success(ticketEventService.getSchedules(eventId));
    }

    @Operation(summary = "[STEP 2] 좌석 구역 목록 조회 (잔여 좌석 포함)")
    @GetMapping("/schedules/{scheduleId}/seat-zones")
    public ApiResponse<List<SeatZoneResponse>> getSeatZones(@PathVariable Long scheduleId) {
        return ApiResponse.success(ticketEventService.getSeatZones(scheduleId));
    }
}
