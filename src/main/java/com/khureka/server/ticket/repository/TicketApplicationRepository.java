package com.khureka.server.ticket.repository;

import com.khureka.server.domain.ApplicationStatus;
import com.khureka.server.domain.TicketApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 응모 Repository.
 *
 * Step 5: 응모 완료
 * Step 6: 추첨 대상 응모자 조회
 * Step 11: 티켓 보관함 / 마이페이지 응모 내역 조회
 */
public interface TicketApplicationRepository extends JpaRepository<TicketApplication, Long> {

    /**
     * 특정 일정의 특정 상태 응모자 목록 조회.
     *
     * Step 6: 추첨 시 APPLIED 상태 응모자 전체 조회.
     */
    List<TicketApplication> findByScheduleIdAndStatus(Long scheduleId, ApplicationStatus status);

    /**
     * 동일 사용자가 같은 일정에 응모했는지 확인.
     *
     * Step 5: 중복 응모 방지.
     */
    boolean existsByUserIdAndScheduleId(Long userId, Long scheduleId);

    /**
     * 사용자의 전체 응모 내역 조회 (마이페이지).
     *
     * 최신 응모순 정렬.
     */
    List<TicketApplication> findByUserIdOrderByAppliedAtDesc(Long userId);

    /**
     * 사용자의 특정 상태 응모 내역 조회.
     *
     * Step 11: 티켓 보관함 (status = TICKET_ISSUED).
     */
    List<TicketApplication> findByUserIdAndStatusOrderByAppliedAtDesc(Long userId, ApplicationStatus status);

    /**
     * 사용자의 특정 일정 응모 조회.
     */
    Optional<TicketApplication> findByUserIdAndScheduleId(Long userId, Long scheduleId);

    /**
     * 티켓 보관함 조회 (이벤트 정보 JOIN FETCH).
     *
     * Step 11: N+1 방지를 위해 연관 엔티티를 한번에 로딩.
     */
    @Query("""
        SELECT a FROM TicketApplication a
        JOIN FETCH a.schedule s
        JOIN FETCH s.event
        JOIN FETCH a.assignedSeatZone
        WHERE a.user.id = :userId
          AND a.status = com.khureka.server.domain.ApplicationStatus.TICKET_ISSUED
        ORDER BY s.startTime ASC
    """)
    List<TicketApplication> findTicketsForWallet(@Param("userId") Long userId);

    /**
     * 마이페이지 응모 내역 조회 (이벤트 + 구역 정보 JOIN FETCH).
     *
     * N+1 방지를 위해 연관 엔티티를 한번에 로딩.
     */
    @Query("""
        SELECT a FROM TicketApplication a
        JOIN FETCH a.schedule s
        JOIN FETCH s.event
        JOIN FETCH a.priority1SeatZone
        LEFT JOIN FETCH a.priority2SeatZone
        LEFT JOIN FETCH a.priority3SeatZone
        LEFT JOIN FETCH a.assignedSeatZone
        WHERE a.user.id = :userId
        ORDER BY a.appliedAt DESC
    """)
    List<TicketApplication> findApplicationHistory(@Param("userId") Long userId);
}
