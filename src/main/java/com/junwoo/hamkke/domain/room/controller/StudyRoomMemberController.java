package com.junwoo.hamkke.domain.room.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.room.dto.EnterStudyRoomRequest;
import com.junwoo.hamkke.domain.room.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room.service.StudyRoomMemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private final StudyRoomMemberService studyRoomMemberService;

    @PostMapping("/{roomId}/enter")
    public ResponseEntity<Void> enterRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) EnterStudyRoomRequest request
    ) {

        log.info("[StudyRoomMemberController] enterRoom: {}", roomId);

        Long userId = userDetails.getUser().id();

        studyRoomMemberService.enterRoom(roomId, userId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("{roomId}")
    public ResponseEntity<List<StudyRoomMemberResponse>> getStudyRoomMembers(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(studyRoomMemberService.getStudyRoomMembers(roomId));
    }
}
