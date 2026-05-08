package com.khureka.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 어르신들을 위한 신체적 특성 기반 좌석 선호도.
 */
@Getter
@RequiredArgsConstructor
public enum SeatPreference {
    NONE("선택 안 함"),
    EYESIGHT("앞좌석 우선 (눈이 침침하신 분)"),
    LEG("통로석 우선 (다리가 불편하신 분)"),
    HEARING("뒷좌석 우선 (청력이 예민하신 분)");

    private final String description;
}
