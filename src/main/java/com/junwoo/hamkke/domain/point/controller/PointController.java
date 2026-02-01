package com.junwoo.hamkke.domain.point.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.point.dto.PointHistoryResponse;
import com.junwoo.hamkke.domain.point.dto.PointSummaryResponse;
import com.junwoo.hamkke.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/me/summary")
    public ResponseEntity<PointSummaryResponse> getMyPointSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();

        PointSummaryResponse response = pointService.getPointSummary(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/history")
    public ResponseEntity<List<PointHistoryResponse>> getMyPointHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();
        List<PointHistoryResponse> responses = pointService.getPointHistory(userId);

        return ResponseEntity.ok(responses);
    }
}
