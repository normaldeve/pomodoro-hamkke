package com.junwoo.hamkke.domain.reflection.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.image.ImageDirectory;
import com.junwoo.hamkke.domain.image.ImageUploader;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionQueryResponse;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.service.ReflectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@RestController
@RequestMapping("/api/reflections")
@RequiredArgsConstructor
public class ReflectionController {

    private final ImageUploader imageUploader;
    private final ReflectionService reflectionService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestPart MultipartFile file
    ) {
        String imageUrl = imageUploader.upload(file, ImageDirectory.REFLECTION);

        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<ReflectionResponse>> getRoomReflections(
            @PathVariable Long roomId
    ) {
        List<ReflectionResponse> response = reflectionService.getRoomReflections(roomId);

        return ResponseEntity.ok(response);
    }

    /**
     * 내 회고 조회
     * - date만 있으면: 특정 날 조회
     * - year, month만 있으면: 특정 월 조회
     * - 아무것도 없으면: 전체 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReflectionQueryResponse>> getMyReflections(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().id();

        List<ReflectionQueryResponse> reflections = reflectionService.getReflections(userId, date, year, month);

        return ResponseEntity.ok(reflections);
    }
}
