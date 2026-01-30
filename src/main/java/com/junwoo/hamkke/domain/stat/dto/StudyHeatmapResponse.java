package com.junwoo.hamkke.domain.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Getter
@AllArgsConstructor
public class StudyHeatmapResponse {

    private int year;
    private List<DayRecord> records;

    @Getter
    @AllArgsConstructor
    public static class DayRecord {
        private LocalDate date;
        private int level;
        private int totalMinutes;
    }
}
