package com.khureka.server.domain;

import com.khureka.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공연/경기 기본 정보 Entity.
 *
 * 하나의 TicketEvent는 여러 EventSchedule(일정)을 가질 수 있다.
 * 예: "임영웅 전국투어 콘서트" → 5/10 공연, 5/11 공연
 */
@Entity
@Table(name = "ticket_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventCategory category;

    @Column(length = 200)
    private String keyword;

    @Column(nullable = false, length = 100)
    private String venueName;

    @Column(length = 255)
    private String venueAddress;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Builder
    public TicketEvent(String title, EventCategory category, String keyword,
            String venueName, String venueAddress,
            String description, String thumbnailUrl) {
        this.title = title;
        this.category = category;
        this.keyword = keyword;
        this.venueName = venueName;
        this.venueAddress = venueAddress;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }
}
