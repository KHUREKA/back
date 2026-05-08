package com.khureka.server.ticket.controller;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.security.CustomUserDetails;
import com.khureka.server.ticket.dto.ApplicationRequest;
import com.khureka.server.ticket.dto.ApplicationResponse;
import com.khureka.server.ticket.dto.TicketResponse;
import com.khureka.server.ticket.service.TicketApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 응모 및 티켓 조회 API.
 *
 * Step 3~5: 응모
 * Step 11: 티켓 보관함 / 마이페이지
 */
@Tag(name = "Ticket Application", description = "응모 및 티켓 조회 API")
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class TicketApplicationController {

    private final TicketApplicationService applicationService;

    @Operation(summary = "응모하기", description = "좌석 개수 + 구역 선택(수동/자동) 후 응모")
    @PostMapping
    public ApiResponse<ApplicationResponse> apply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ApplicationRequest request) {

        return ApiResponse.success(applicationService.apply(userDetails.getUserId(), request));
    }

    @Operation(summary = "내 응모 내역 조회", description = "마이페이지 — 전체 응모 내역")
    @GetMapping("/me")
    public ApiResponse<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(applicationService.getMyApplications(userDetails.getUserId()));
    }

    @Operation(summary = "내 티켓 보관함", description = "당첨 + 발급 완료된 티켓 목록")
    @GetMapping("/me/tickets")
    public ApiResponse<List<TicketResponse>> getMyTickets(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(applicationService.getMyTickets(userDetails.getUserId()));
    }

    @Operation(summary = "응모 취소", description = "추첨 전 응모 내역 취소")
    @DeleteMapping("/{applicationId}")
    public ApiResponse<Void> cancelApplication(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {

        applicationService.cancelApplication(userDetails.getUserId(), applicationId);
        return ApiResponse.success(null);
    }
}

