package com.khureka.server.ticket.repository;

import com.khureka.server.domain.EventCategory;
import com.khureka.server.domain.TicketEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 공연/경기 Repository.
 *
 * Step 1: 사용자가 공연/경기 검색
 */
public interface TicketEventRepository extends JpaRepository<TicketEvent, Long> {

    /**
     * 카테고리로 공연/경기 목록 조회.
     */
    List<TicketEvent> findByCategory(EventCategory category);

    /**
     * 제목, 키워드, 장소명을 통합 검색 (LIKE).
     *
     * 예: keyword = "임영웅" → title, keyword, venueName 중 하나라도 포함하면 매칭
     */
    @Query("""
        SELECT e FROM TicketEvent e
        WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.keyword) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.venueName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<TicketEvent> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 카테고리 + 키워드 복합 검색.
     */
    @Query("""
        SELECT e FROM TicketEvent e
        WHERE e.category = :category
          AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.keyword) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.venueName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<TicketEvent> searchByCategoryAndKeyword(
            @Param("category") EventCategory category,
            @Param("keyword") String keyword
    );
}
