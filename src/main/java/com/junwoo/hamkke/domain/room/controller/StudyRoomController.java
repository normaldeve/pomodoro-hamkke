package com.junwoo.hamkke.domain.room.controller;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.room.dto.CreateStudyRoomRequest;
import com.junwoo.hamkke.domain.room.dto.StudyRoomResponse;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.service.StudyRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping("/create")
    public ResponseEntity<StudyRoomResponse> createStudyRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateStudyRoomRequest request
            )
    {
        if (request.secret() && (request.password() == null || request.password().isBlank())) {
            throw new StudyRoomException(ErrorCode.SECRET_ROOM_NEED_PASSWORD);
        }

        Long hostId = userDetails.getUser().id();

        StudyRoomResponse response = studyRoomService.createRoom(hostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<StudyRoomResponse>> getStudyRooms(
            @RequestParam(defaultValue = "0") int page
    ) {

        return ResponseEntity.ok(studyRoomService.getStudyRooms(page));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<StudyRoomResponse> getStudyRoom(
            @PathVariable Long roomId
    ) {

        return ResponseEntity.ok(studyRoomService.getStudyRoom(roomId));
    }
}