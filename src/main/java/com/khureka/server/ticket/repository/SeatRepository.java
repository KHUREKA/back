package com.khureka.server.ticket.repository;

import com.khureka.server.domain.Seat;
import com.khureka.server.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 실제 좌석 Repository.
 *
 * Step 7: 구역 내 잔여 좌석 수 확인
 * Step 8: 구역 내 실제 좌석 랜덤 배정
 */
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * 특정 구역의 특정 상태 좌석 수 조회.
     *
     * Step 7: 1순위 구역에 AVAILABLE 좌석이 requestedSeatCount 이상인지 확인.
     */
    long countBySeatZoneIdAndStatus(Long seatZoneId, SeatStatus status);

    /**
     * 특정 구역에서 요청 수량만큼 AVAILABLE 좌석을 랜덤으로 조회.
     *
     * Step 8: 배정된 구역 내에서 실제 좌석을 랜덤 배정.
     * MySQL의 RAND()를 사용하여 랜덤 정렬 후 LIMIT.
     *
     * 주의: 조회 결과 개수가 count보다 작으면 배정 불가.
     */
    @Query(value = """
        SELECT * FROM seats
        WHERE seat_zone_id = :zoneId
          AND status = 'AVAILABLE'
        ORDER BY RAND()
        LIMIT :count
    """, nativeQuery = true)
    List<Seat> findRandomAvailableSeats(
            @Param("zoneId") Long zoneId,
            @Param("count") int count
    );

    /**
     * 특정 구역의 전체 좌석 조회.
     */
    List<Seat> findBySeatZoneId(Long seatZoneId);
}
