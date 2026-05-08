package com.khureka.server.ticket.repository;

import com.khureka.server.domain.EventSchedule;
import com.khureka.server.domain.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 Repository.
 *
 * Step 2: 일정 선택
 * Step 6: 추첨 대상 일정 조회
 */
public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

    /**
     * 특정 공연의 일정 목록 조회.
     */
    List<EventSchedule> findByEventIdOrderByStartTimeAsc(Long eventId);

    /**
     * 특정 공연의 특정 상태 일정 조회.
     */
    List<EventSchedule> findByEventIdAndStatus(Long eventId, ScheduleStatus status);

    /**
     * 현재 응모 가능한 일정 조회.
     *
     * status = APPLICATION_OPEN 이고 현재 시간이 응모 기간 내인 일정.
     */
    @Query("""
        SELECT s FROM EventSchedule s
        WHERE s.event.id = :eventId
          AND s.status = 'APPLICATION_OPEN'
          AND :now BETWEEN s.applicationOpenAt AND s.applicationCloseAt
        ORDER BY s.startTime ASC
    """)
    List<EventSchedule> findOpenSchedules(
            @Param("eventId") Long eventId,
            @Param("now") LocalDateTime now
    );

    /**
     * 추첨 시간이 지났지만 아직 추첨이 실행되지 않은 일정 조회.
     *
     * 스케줄러가 주기적으로 호출하여 추첨 대상을 찾는다.
     */
    @Query("""
        SELECT s FROM EventSchedule s
        WHERE s.status = 'APPLICATION_CLOSED'
          AND s.lotteryAt <= :now
    """)
    List<EventSchedule> findSchedulesReadyForLottery(@Param("now") LocalDateTime now);
}
