package com.junwoo.hamkke.domain.room.controller;

import com.junwoo.hamkke.domain.room.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room.service.StudyRoomMemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
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
