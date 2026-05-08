package com.khureka.server.ticket.repository;

import com.khureka.server.domain.SeatZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 좌석 구역 Repository.
 *
 * Step 4: 좌석 구역 목록 조회
 * Step 7: 1~3순위 외 남은 구역 자동 배정
 */
public interface SeatZoneRepository extends JpaRepository<SeatZone, Long> {

    /**
     * 특정 일정의 좌석 구역 목록 조회.
     */
    List<SeatZone> findByScheduleIdOrderByPriceDesc(Long scheduleId);

    /**
     * 특정 일정의 좌석 구역 목록 (잔여 좌석 수 포함).
     *
     * Step 4 UI에서 각 구역의 잔여석을 표시할 때 사용.
     */
    @Query("""
        SELECT z FROM SeatZone z
        WHERE z.schedule.id = :scheduleId
        ORDER BY z.price DESC
    """)
    List<SeatZone> findByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 1~3순위 구역 외에 좌석이 충분한 구역 탐색 (자동 배정용).
     *
     * Step 7-④: 1~3순위 모두 불가 시, 남은 구역 중 AVAILABLE 좌석 >= requiredCount 인 구역.
     * MySQL의 RAND()로 랜덤 선택.
     */
    @Query("""
        SELECT z FROM SeatZone z
        WHERE z.schedule.id = :scheduleId
          AND z.id NOT IN :excludedZoneIds
          AND (SELECT COUNT(s) FROM Seat s
               WHERE s.seatZone = z AND s.status = com.khureka.server.domain.SeatStatus.AVAILABLE) >= :requiredCount
        ORDER BY FUNCTION('RAND')
    """)
    List<SeatZone> findAvailableZonesExcluding(
            @Param("scheduleId") Long scheduleId,
            @Param("excludedZoneIds") List<Long> excludedZoneIds,
            @Param("requiredCount") int requiredCount
    );

    /**
     * 전체 구역 중 좌석이 충분한 구역 탐색 (autoAssign용).
     *
     * autoAssign = true 일 때, 모든 구역 대상으로 탐색.
     */
    @Query("""
        SELECT z FROM SeatZone z
        WHERE z.schedule.id = :scheduleId
          AND (SELECT COUNT(s) FROM Seat s
               WHERE s.seatZone = z AND s.status = com.khureka.server.domain.SeatStatus.AVAILABLE) >= :requiredCount
        ORDER BY FUNCTION('RAND')
    """)
    List<SeatZone> findAnyAvailableZone(
            @Param("scheduleId") Long scheduleId,
            @Param("requiredCount") int requiredCount
    );
}
