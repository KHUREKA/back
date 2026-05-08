package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.*;
import com.khureka.server.ticket.dto.LotteryResultResponse;
import com.khureka.server.ticket.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 추첨 서비스.
 *
 * Step 6: 추첨 실행
 * Step 7: 좌석 구역 배정 (1순위 → 2순위 → 3순위 → 자동 배정 → 미당첨)
 * Step 8: 구역 내 실제 좌석 랜덤 배정
 * Step 9: Mock 자동 결제
 * Step 10: 티켓 발급
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryService {

    private final TicketApplicationRepository applicationRepository;
    private final ApplicationAssignedSeatRepository assignedSeatRepository;
    private final EventScheduleRepository scheduleRepository;
    private final SeatZoneRepository seatZoneRepository;
    private final SeatRepository seatRepository;

    /**
     * 특정 일정에 대해 추첨을 실행한다.
     *
     * 전체 흐름:
     * 1. APPLIED 응모자 전체 조회
     * 2. SecureRandom으로 랜덤 셔플
     * 3. 응모자 한 명씩 처리
     * 4. 1순위 → 2순위 → 3순위 → 자동 배정 → 미당첨
     * 5. 배정 성공 시: 좌석 랜덤 배정 + Mock 결제 + 티켓 발급
     * 6. 일정 상태를 LOTTERY_DONE으로 변경
     */
    @Transactional
    public LotteryResultResponse drawLottery(Long scheduleId) {
        // 일정 조회
        EventSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 이미 추첨된 일정인지 확인
        if (schedule.getStatus() == ScheduleStatus.LOTTERY_DONE
                || schedule.getStatus() == ScheduleStatus.FINISHED) {
            throw new BusinessException(ErrorCode.LOTTERY_ALREADY_DONE);
        }

        // Step 6: APPLIED 응모자 조회
        List<TicketApplication> applications =
                applicationRepository.findByScheduleIdAndStatus(scheduleId, ApplicationStatus.APPLIED);

        // 랜덤 셔플
        List<TicketApplication> shuffled = new ArrayList<>(applications);
        Collections.shuffle(shuffled, new SecureRandom());

        int winnersCount = 0;
        int losersCount = 0;

        // Step 7~10: 응모자 한 명씩 처리
        for (TicketApplication application : shuffled) {
            int requestedCount = application.getRequestedSeatCount();

            // Step 7: 좌석 구역 배정
            List<Seat> selectedSeats;

            if (Boolean.TRUE.equals(application.getAutoAssign())) {
                // 자동 선택 모드: 모든 구역 대상
                selectedSeats = findSeatsFromAnyZone(scheduleId, requestedCount);
            } else {
                // 수동 선택 모드: 1순위 → 2순위 → 3순위 순서
                selectedSeats = findSeatsByPriority(application, requestedCount);

                // 1~3순위 모두 실패 → 남은 구역 자동 배정
                if (selectedSeats.size() < requestedCount) {
                    selectedSeats = findSeatsFromRemainingZones(application, scheduleId, requestedCount);
                }
            }

            // 배정 불가 → 미당첨
            if (selectedSeats.size() < requestedCount) {
                application.markLose();
                losersCount++;
                log.info("미당첨 처리: applicationId={}, userId={}",
                        application.getId(), application.getUser().getId());
                continue;
            }

            // Step 8: 좌석 배정 확정
            SeatZone assignedZone = selectedSeats.get(0).getSeatZone();
            String appCode = "APP-" + application.getId() + "-" + System.currentTimeMillis();

            // Step 9 + 10: Mock 결제 + 티켓 발급
            application.issueTicket(assignedZone, appCode);

            for (Seat seat : selectedSeats) {
                // 좌석 상태 변경
                seat.assignSeat();

                // 배정 좌석 기록 + 티켓 코드 생성
                String ticketCode = "TICKET-" + application.getId() + "-" + seat.getId();
                ApplicationAssignedSeat assignedSeat = ApplicationAssignedSeat.builder()
                        .application(application)
                        .seat(seat)
                        .ticketCode(ticketCode)
                        .build();
                assignedSeatRepository.save(assignedSeat);
            }

            winnersCount++;
            log.info("당첨 처리: applicationId={}, userId={}, zone={}, seats={}",
                    application.getId(), application.getUser().getId(),
                    assignedZone.getName(), selectedSeats.size());
        }

        // Step 6: 일정 상태 변경
        schedule.markLotteryDone();

        log.info("추첨 완료: scheduleId={}, total={}, winners={}, losers={}",
                scheduleId, shuffled.size(), winnersCount, losersCount);

        return LotteryResultResponse.of(scheduleId, shuffled.size(), winnersCount, losersCount);
    }

    /**
     * 1순위 → 2순위 → 3순위 구역 순서로 좌석 탐색.
     *
     * 각 구역에서 AVAILABLE 좌석이 requestedCount 이상 있으면 랜덤으로 선택.
     */
    private List<Seat> findSeatsByPriority(TicketApplication application, int requestedCount) {
        List<SeatZone> priorityZones = new ArrayList<>();

        if (application.getPriority1SeatZone() != null) {
            priorityZones.add(application.getPriority1SeatZone());
        }
        if (application.getPriority2SeatZone() != null) {
            priorityZones.add(application.getPriority2SeatZone());
        }
        if (application.getPriority3SeatZone() != null) {
            priorityZones.add(application.getPriority3SeatZone());
        }

        for (SeatZone zone : priorityZones) {
            List<Seat> seats = findContiguousOrRandomSeats(zone.getId(), requestedCount);
            if (seats.size() == requestedCount) {
                return seats;
            }
        }

        return List.of();
    }

    /**
     * 1~3순위 구역 외 나머지 구역에서 좌석 탐색.
     *
     * Step 7-④: 1~3순위 모두 불가능 시, 선택하지 않은 다른 구역 중
     * AVAILABLE 좌석이 requestedCount 이상인 구역을 랜덤으로 찾아 배정.
     */
    private List<Seat> findSeatsFromRemainingZones(
            TicketApplication application, Long scheduleId, int requestedCount) {

        List<Long> excludedZoneIds = new ArrayList<>();
        if (application.getPriority1SeatZone() != null) {
            excludedZoneIds.add(application.getPriority1SeatZone().getId());
        }
        if (application.getPriority2SeatZone() != null) {
            excludedZoneIds.add(application.getPriority2SeatZone().getId());
        }
        if (application.getPriority3SeatZone() != null) {
            excludedZoneIds.add(application.getPriority3SeatZone().getId());
        }

        // 제외할 구역이 없으면 빈 리스트 방지 (JPA IN 절에 빈 리스트 전달 방지)
        if (excludedZoneIds.isEmpty()) {
            excludedZoneIds.add(-1L);
        }

        List<SeatZone> availableZones = seatZoneRepository.findAvailableZonesExcluding(
                scheduleId, excludedZoneIds, requestedCount);

        if (availableZones.isEmpty()) {
            return List.of();
        }

        // 첫 번째 구역 (이미 RAND()로 정렬됨)
        SeatZone selectedZone = availableZones.get(0);
        return findContiguousOrRandomSeats(selectedZone.getId(), requestedCount);
    }

    /**
     * 전체 구역에서 좌석 탐색 (autoAssign 모드).
     */
    private List<Seat> findSeatsFromAnyZone(Long scheduleId, int requestedCount) {
        List<SeatZone> availableZones = seatZoneRepository.findAnyAvailableZone(
                scheduleId, requestedCount);

        if (availableZones.isEmpty()) {
            return List.of();
        }

        SeatZone selectedZone = availableZones.get(0);
        return findContiguousOrRandomSeats(selectedZone.getId(), requestedCount);
    }

    /**
     * 구역 내에서 연속된 좌석(연석)을 우선적으로 찾고, 없으면 랜덤으로 배정한다.
     */
    private List<Seat> findContiguousOrRandomSeats(Long zoneId, int requestedCount) {
        List<Seat> availableSeats = seatRepository.findBySeatZoneIdAndStatus(zoneId, SeatStatus.AVAILABLE);
        if (availableSeats.size() < requestedCount) return List.of();

        if (requestedCount == 1) {
            List<Seat> shuffled = new ArrayList<>(availableSeats);
            Collections.shuffle(shuffled, new SecureRandom());
            return shuffled.subList(0, 1);
        }

        // 행(row)별로 좌석 그룹화
        java.util.Map<String, List<Seat>> seatsByRow = availableSeats.stream()
                .collect(java.util.stream.Collectors.groupingBy(Seat::getRowLabel));

        List<List<Seat>> contiguousBlocks = new ArrayList<>();

        for (List<Seat> rowSeats : seatsByRow.values()) {
            // 좌석 번호 순으로 정렬
            rowSeats.sort((s1, s2) -> {
                try {
                    return Integer.compare(Integer.parseInt(s1.getSeatNumber()), Integer.parseInt(s2.getSeatNumber()));
                } catch (NumberFormatException e) {
                    return s1.getSeatNumber().compareTo(s2.getSeatNumber());
                }
            });

            // 연속된 좌석 블록 찾기
            for (int i = 0; i <= rowSeats.size() - requestedCount; i++) {
                boolean isContiguous = true;
                for (int j = 0; j < requestedCount - 1; j++) {
                    try {
                        int currentNum = Integer.parseInt(rowSeats.get(i + j).getSeatNumber());
                        int nextNum = Integer.parseInt(rowSeats.get(i + j + 1).getSeatNumber());
                        if (nextNum != currentNum + 1) {
                            isContiguous = false;
                            break;
                        }
                    } catch (NumberFormatException e) {
                        isContiguous = false;
                        break;
                    }
                }
                if (isContiguous) {
                    contiguousBlocks.add(new ArrayList<>(rowSeats.subList(i, i + requestedCount)));
                }
            }
        }

        // 연석 블록이 존재하면 그 중 하나를 랜덤하게 선택
        if (!contiguousBlocks.isEmpty()) {
            return contiguousBlocks.get(new SecureRandom().nextInt(contiguousBlocks.size()));
        }

        // 연석이 없다면 동일 구역 내에서 랜덤으로 흩어진 좌석 배정
        List<Seat> shuffled = new ArrayList<>(availableSeats);
        Collections.shuffle(shuffled, new SecureRandom());
        return shuffled.subList(0, requestedCount);
    }
}
