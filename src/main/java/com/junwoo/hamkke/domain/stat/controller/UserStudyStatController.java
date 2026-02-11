package com.junwoo.hamkke.domain.stat.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.stat.dto.MonthlyStudyStatResponse;
import com.junwoo.hamkke.domain.stat.dto.StudyHeatmapResponse;
import com.junwoo.hamkke.domain.stat.dto.StudyTimeSummaryResponse;
import com.junwoo.hamkke.domain.stat.service.UserStudyStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@RestController
@RequestMapping("/api/stats/me")
@RequiredArgsConstructor
public class UserStudyStatController {

    private final UserStudyStatService studyStatService;

    @GetMapping
    public ResponseEntity<StudyHeatmapResponse> getStudyHeatmap(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year
    ) {
        Long userId = userDetails.getUser().id();

        StudyHeatmapResponse response = studyStatService.getYearlyStudyRecords(userId, year);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<StudyTimeSummaryResponse> getStudyTimeSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();

        StudyTimeSummaryResponse response = studyStatService.getStudyTimeSummary(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyStudyStatResponse> getMonthlyStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long userId = userDetails.getUser().id();
        MonthlyStudyStatResponse response = studyStatService.getMonthlyStatistics(userId, year, month);
        return ResponseEntity.ok(response);
    }
}
