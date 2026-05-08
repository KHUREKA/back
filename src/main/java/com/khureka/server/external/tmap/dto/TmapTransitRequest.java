package com.khureka.server.external.tmap.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TmapTransitRequest {
    private String startX;
    private String startY;
    private String endX;
    private String endY;
    private int count;
    private int lang;
    private String format;
}
