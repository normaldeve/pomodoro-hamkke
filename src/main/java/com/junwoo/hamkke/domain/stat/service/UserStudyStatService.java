package com.junwoo.hamkke.domain.stat.service;

import com.junwoo.hamkke.domain.stat.dto.MonthlyStudyStatResponse;
import com.junwoo.hamkke.domain.stat.dto.StudyHeatmapResponse;
import com.junwoo.hamkke.domain.stat.dto.StudyTimeSummaryResponse;
import com.junwoo.hamkke.domain.stat.entity.UserDailyStudyStat;
import com.junwoo.hamkke.domain.stat.repository.UserDailyStudyStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserStudyStatService {

    private final UserDailyStudyStatRepository dailyStatRepository;

    public StudyHeatmapResponse getYearlyStudyRecords(Long userId, int year) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<UserDailyStudyStat> stats = dailyStatRepository.findAllByUserIdAndStudyDateBetween(userId, start, end);

        List<StudyHeatmapResponse.DayRecord> records = stats.stream()
                .map(stat -> new StudyHeatmapResponse.DayRecord(
                        stat.getStudyDate(), stat.getLevel(), stat.getTotalMinutes()
                )).toList();

        return new StudyHeatmapResponse(year, records);
    }

    // 오늘, 이번 주, 이번 달 공부 시간 요약
    public StudyTimeSummaryResponse getStudyTimeSummary(Long userId) {
        LocalDate today = LocalDate.now();

        int todayMinutes = dailyStatRepository.sumMinutesByPeriod(userId, today, today);

        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int thisWeekMinutes = dailyStatRepository.sumMinutesByPeriod(userId, weekStart, today);

        LocalDate monthStart = today.withDayOfMonth(1);
        int thisMonthMinutes = dailyStatRepository.sumMinutesByPeriod(userId, monthStart, today);

        int consecutiveDays = calculateConsecutiveDays(userId, today);

        return StudyTimeSummaryResponse.builder()
                .todayMinutes(todayMinutes)
                .thisWeekMinutes(thisWeekMinutes)
                .thisMonthMinutes(thisMonthMinutes)
                .consecutiveDays(consecutiveDays)
                .build();
    }

    // 월별 공부 시간 통계 (특정 월을 기준으로 6개월 데이터를 보입니다)
    public MonthlyStudyStatResponse getMonthlyStatistics(Long userId, int year, int month) {
        // 기준 년월 생성
        YearMonth baseYearMonth = YearMonth.of(year, month);

        // 시작일: 기준 월의 1일
        LocalDate startDate = baseYearMonth.atDay(1);

        // 종료일: 기준 월로부터 5개월 후의 마지막 날
        YearMonth endYearMonth = baseYearMonth.plusMonths(5);
        LocalDate endDate = endYearMonth.atEndOfMonth();

        // 데이터 조회
        List<Object[]> rawResults = dailyStatRepository.getMonthlyStudyMinutes(userId, startDate, endDate);

        List<MonthlyStudyStatResponse.MonthRecord> monthlyRecords = new ArrayList<>();

        for (Object[] row : rawResults) {
            int resultYear = ((Number) row[0]).intValue();
            int resultMonth = ((Number) row[1]).intValue();
            int totalMinutes = ((Number) row[2]).intValue();

            monthlyRecords.add(new MonthlyStudyStatResponse.MonthRecord(
                    resultYear,
                    resultMonth,
                    totalMinutes
            ));
        }

        // 공부하지 않은 월은 0으로 채우기
        fillEmptyMonths(monthlyRecords, baseYearMonth, 6);

        return new MonthlyStudyStatResponse(
                baseYearMonth.getYear(),
                baseYearMonth.getMonthValue(),
                endYearMonth.getYear(),
                endYearMonth.getMonthValue(),
                monthlyRecords
        );
    }

    private int calculateConsecutiveDays(Long userId, LocalDate today) {
        List<LocalDate> studyDates = dailyStatRepository.findRecentStudyDates(userId, today);

        if (studyDates.isEmpty()) {
            return 0;
        }

        int consecutiveDays = 0;
        LocalDate expectedDate = today;

        for (LocalDate date : studyDates) {
            if (date.equals(expectedDate)) {
                consecutiveDays++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }

        return consecutiveDays;
    }

    /**
     * 공부하지 않은 월을 0으로 채우기
     */
    private void fillEmptyMonths(
            List<MonthlyStudyStatResponse.MonthRecord> records,
            YearMonth startYearMonth,
            int monthCount
    ) {
        for (int i = 0; i < monthCount; i++) {
            YearMonth targetYearMonth = startYearMonth.plusMonths(i);
            final int targetYear = targetYearMonth.getYear();
            final int targetMonth = targetYearMonth.getMonthValue();

            boolean exists = records.stream()
                    .anyMatch(r -> r.getYear() == targetYear && r.getMonth() == targetMonth);

            if (!exists) {
                records.add(new MonthlyStudyStatResponse.MonthRecord(targetYear, targetMonth, 0));
            }
        }

        // 년도, 월 순서로 정렬
        records.sort((a, b) -> {
            if (a.getYear() != b.getYear()) {
                return Integer.compare(a.getYear(), b.getYear());
            }
            return Integer.compare(a.getMonth(), b.getMonth());
        });
    }
}
