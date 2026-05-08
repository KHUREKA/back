package com.khureka.server.ticket.controller;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.ticket.dto.LotteryResultResponse;
import com.khureka.server.ticket.service.LotteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 추첨 관리 API (관리자용).
 *
 * Step 6~10: 추첨 실행 → 좌석 배정 → Mock 결제 → 티켓 발급
 */
@Tag(name = "Lottery", description = "추첨 관리 API (관리자용)")
@RestController
@RequestMapping("/api/v1/admin/lottery")
@RequiredArgsConstructor
public class LotteryController {

    private final LotteryService lotteryService;

    @Operation(summary = "추첨 실행", description = "특정 일정에 대해 추첨을 수동 실행합니다 (관리자용)")
    @PostMapping("/{scheduleId}")
    public ApiResponse<LotteryResultResponse> drawLottery(@PathVariable Long scheduleId) {
        return ApiResponse.success(lotteryService.drawLottery(scheduleId));
    }
}
