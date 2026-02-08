package com.junwoo.hamkke.domain.plan.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.plan.dto.CreatePlanRequest;
import com.junwoo.hamkke.domain.plan.dto.PlanResponse;
import com.junwoo.hamkke.domain.plan.dto.UpdatePlanRequest;
import com.junwoo.hamkke.domain.plan.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Plan API Controller
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /**
     * 계획 생성
     */
    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePlanRequest request
    ) {

        Long userId = userDetails.getUser().id();

        PlanResponse response = planService.createPlan(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 계획 수정
     */
    @PutMapping("/{planId}")
    public ResponseEntity<PlanResponse> updatePlan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long planId,
            @Valid @RequestBody UpdatePlanRequest request
    ) {

        Long userId = userDetails.getUser().id();

        PlanResponse response = planService.updatePlan(userId, planId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 계획 완료 상태 토글
     */
    @PatchMapping("/{planId}/toggle")
    public ResponseEntity<PlanResponse> togglePlanCompleted(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long planId
    ) {
        Long userId = userDetails.getUser().id();

        PlanResponse response = planService.togglePlanCompleted(userId, planId);
        return ResponseEntity.ok(response);
    }

    /**
     * 계획 삭제
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long planId
    ) {
        Long userId = userDetails.getUser().id();

        planService.deletePlan(userId, planId);
        return ResponseEntity.ok(null);
    }

    /**
     * 특정 날짜의 계획 조회
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<PlanResponse>> getPlansByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = userDetails.getUser().id();

        List<PlanResponse> response = planService.getPlansByDate(userId, date);
        return ResponseEntity.ok(response);
    }

    /**
     * 기간별 계획 조회 (월별 캘린더용)
     */
    @GetMapping("/range")
    public ResponseEntity<List<PlanResponse>> getPlansByDateRange(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Long userId = userDetails.getUser().id();

        List<PlanResponse> response = planService.getPlansByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * 미완료 계획 개수 조회
     */
    @GetMapping("/incomplete/count")
    public ResponseEntity<Long> getIncompleteCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();

        long count = planService.getIncompleteCount(userId);
        return ResponseEntity.ok(count);
    }
}