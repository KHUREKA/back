package com.khureka.server.ticket.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 추첨 결과 응답 DTO.
 */
@Getter
@Builder
public class LotteryResultResponse {

    private Long scheduleId;
    private int totalApplicants;
    private int winnersCount;
    private int losersCount;

    public static LotteryResultResponse of(Long scheduleId, int total, int winners, int losers) {
        return LotteryResultResponse.builder()
                .scheduleId(scheduleId)
                .totalApplicants(total)
                .winnersCount(winners)
                .losersCount(losers)
                .build();
    }
}
