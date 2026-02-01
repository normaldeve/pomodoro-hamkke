package com.junwoo.hamkke.domain.point.service;

import com.junwoo.hamkke.domain.point.dto.PointHistoryResponse;
import com.junwoo.hamkke.domain.point.dto.PointSummaryResponse;
import com.junwoo.hamkke.domain.point.entity.PointLogEntity;
import com.junwoo.hamkke.domain.point.entity.PointType;
import com.junwoo.hamkke.domain.point.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointLogRepository pointLogRepository;
    private final PointValidator pointValidator;

    public boolean earnPoint(Long userId, PointType type, int amount, Long refId) {
        // 중복 지급 검증
        pointValidator.validatePointIssuable(type, refId);

        PointLogEntity pointLog = PointLogEntity.builder()
                .userId(userId)
                .type(type)
                .amount(amount)
                .refId(refId)
                .build();

        pointLogRepository.save(pointLog);

        log.info("[PointService] 포인트 적립 성공 - userId: {}, type: {}, amount: {}, refId: {}", userId, type, amount, refId);

        return true;
    }

    @Transactional(readOnly = true)
    public int getTotalPoints(Long userId) {
        return pointLogRepository.getTotalPoints(userId);
    }

    @Transactional(readOnly = true)
    public PointSummaryResponse getPointSummary(Long userId) {

        int totalPoints = getTotalPoints(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weeStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();

        int todayEarned = calculateEarnedPoints(userId, todayStart, now);
        int thisWeekendEarned = calculateEarnedPoints(userId, weeStart, now);
        int thisMonthEarned = calculateEarnedPoints(userId, monthStart, now);

        return PointSummaryResponse.builder()
                .totalPoints(totalPoints)
                .todayEarned(todayEarned)
                .thisWeekendEarned(thisWeekendEarned)
                .thisMonthEarned(thisMonthEarned)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointHistory(Long userId) {
        return pointLogRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    private int calculateEarnedPoints(Long userId, LocalDateTime start, LocalDateTime end) {
        List<PointLogEntity> logs = pointLogRepository.findByUserIdAndDateRange(userId, start, end);
        return logs.stream()
                .mapToInt(PointLogEntity::getAmount)
                .sum();
    }
}
