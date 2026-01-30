package com.junwoo.hamkke.domain.stat.service;

import com.junwoo.hamkke.domain.stat.dto.StudyHeatmapResponse;
import com.junwoo.hamkke.domain.stat.entity.UserDailyStudyStat;
import com.junwoo.hamkke.domain.stat.repository.UserDailyStudyStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Service
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
}
