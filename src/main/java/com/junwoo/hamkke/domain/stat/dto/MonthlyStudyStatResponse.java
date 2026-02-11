package com.junwoo.hamkke.domain.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 월별 공부 시간 통계 (특정 월 기준 6개월)
 */
@Getter
@AllArgsConstructor
public class MonthlyStudyStatResponse {

    private int startYear;   // 시작 년도
    private int startMonth;  // 시작 월
    private int endYear;     // 종료 년도
    private int endMonth;    // 종료 월
    private List<MonthRecord> monthlyRecords;

    @Getter
    @AllArgsConstructor
    public static class MonthRecord {
        private int year;           // 년도
        private int month;          // 월 (1-12)
        private int totalMinutes;   // 해당 월 총 공부 시간
    }
}