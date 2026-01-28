package com.junwoo.hamkke.domain.reflection.controller;

import com.junwoo.hamkke.domain.image.ImageDirectory;
import com.junwoo.hamkke.domain.image.ImageUploader;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.service.ReflectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
