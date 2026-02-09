package com.junwoo.hamkke.domain.goal.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.goal.dto.CreateStudyGoalRequest;
import com.junwoo.hamkke.domain.goal.dto.StudyGoalResponse;
import com.junwoo.hamkke.domain.goal.service.StudyGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class StudyGoalController {

    private final StudyGoalService studyGoalService;

    @PostMapping("/{roomId}")
    public ResponseEntity<StudyGoalResponse> createGoal(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateStudyGoalRequest request
    ) {

        StudyGoalResponse goal = studyGoalService.createGoal(roomId, userDetails.getUser().id(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<StudyGoalResponse>> getMyGoals(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        return ResponseEntity.ok(studyGoalService.getMyGoals(roomId, userDetails.getUser().id()));
    }

    @PatchMapping("/{goalId}/toggle")
    public ResponseEntity<Void> toggleGoal(
            @PathVariable Long goalId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        studyGoalService.toggleGoal(goalId, userDetails.getUser().id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId
    ) {

        studyGoalService.deleteGoal(goalId);

        return ResponseEntity.noContent().build();
    }
}
