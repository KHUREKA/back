package com.khureka.server.ticket.repository;

import com.khureka.server.domain.ApplicationAssignedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 응모별 배정 좌석 Repository.
 *
 * Step 10: 티켓 발급 (좌석별 ticket_code 저장)
 * Step 11: 티켓 보관함에서 좌석 목록 조회
 */
public interface ApplicationAssignedSeatRepository extends JpaRepository<ApplicationAssignedSeat, Long> {

    /**
     * 특정 응모의 배정 좌석 목록 조회.
     */
    List<ApplicationAssignedSeat> findByApplicationId(Long applicationId);

    /**
     * 특정 응모의 배정 좌석 목록 (좌석 정보 JOIN FETCH).
     *
     * Step 11: 티켓 보관함에서 좌석 상세 조회 시 N+1 방지.
     */
    @Query("""
        SELECT aas FROM ApplicationAssignedSeat aas
        JOIN FETCH aas.seat
        WHERE aas.application.id = :applicationId
        ORDER BY aas.seat.rowLabel ASC, aas.seat.seatNumber ASC
    """)
    List<ApplicationAssignedSeat> findByApplicationIdWithSeat(@Param("applicationId") Long applicationId);

    /**
     * 사용자의 전체 배정 좌석 조회 (티켓 보관함 전체 목록).
     */
    @Query("""
        SELECT aas FROM ApplicationAssignedSeat aas
        JOIN FETCH aas.seat s
        JOIN FETCH aas.application a
        WHERE a.user.id = :userId
          AND a.status = com.khureka.server.domain.ApplicationStatus.TICKET_ISSUED
        ORDER BY a.appliedAt DESC, s.rowLabel ASC, s.seatNumber ASC
    """)
    List<ApplicationAssignedSeat> findAllByUserId(@Param("userId") Long userId);
}
