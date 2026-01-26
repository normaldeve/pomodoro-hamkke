package com.junwoo.hamkke.domain.room_member.controller;

import com.junwoo.hamkke.domain.room_member.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room_member.service.StudyRoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("{roomId}")
    public ResponseEntity<List<StudyRoomMemberResponse>> getStudyRoomMembers(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(studyRoomMemberService.getStudyRoomMembers(roomId));
    }
}
