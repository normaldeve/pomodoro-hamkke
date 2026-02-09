package com.junwoo.hamkke.domain.room_member.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.room_member.dto.ParticipateRoomInfo;
import com.junwoo.hamkke.domain.room_member.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room_member.dto.TransferHostRequests;
import com.junwoo.hamkke.domain.room_member.service.FocusTimeService;
import com.junwoo.hamkke.domain.room_member.service.StudyRoomMemberService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Slf4j
@RestController
@RequestMapping("/api/study-room-members")
@RequiredArgsConstructor
public class StudyRoomMemberController {

    private final FocusTimeService focusTimeService;
    private final StudyRoomMemberService studyRoomMemberService;

    @GetMapping("{roomId}")
    public ResponseEntity<List<StudyRoomMemberResponse>> getStudyRoomMembers(
            @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(studyRoomMemberService.getStudyRoomMembers(roomId));
    }

    @GetMapping("/{roomId}/focus-time/total")
    public ResponseEntity<Integer> getTodayRoomFocusTime(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();

        return ResponseEntity.ok(focusTimeService.getTodayRoomFocusTime(userId, roomId));
    }

    @PostMapping("/{roomId}/transfer-host")
    public ResponseEntity<Void> transferHost(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TransferHostRequests request
    ) {

        Long currentHostId = userDetails.getUser().id();
        studyRoomMemberService.transferHost(roomId, currentHostId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/participate")
    public ResponseEntity<ParticipateRoomInfo> getParticipateRoomInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();

        Optional<ParticipateRoomInfo> participateRoomInfo = studyRoomMemberService.getParticipateRoomInfo(userId);

        return ResponseEntity.ok(participateRoomInfo.orElse(null));

    }
}
