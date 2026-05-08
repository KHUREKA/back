package com.khureka.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeFilter {
    WEEKEND("이번 주말"),
    NEXT_WEEK("다음 주"),
    THIS_MONTH("이번 달 안에"),
    TWO_MONTHS("두 달 안에"),
    ANYTIME("언제든 좋아요");

    private final String description;
}
