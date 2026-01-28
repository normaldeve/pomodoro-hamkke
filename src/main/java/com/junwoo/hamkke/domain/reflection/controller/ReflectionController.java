package com.junwoo.hamkke.domain.reflection.controller;

import com.junwoo.hamkke.domain.image.ImageDirectory;
import com.junwoo.hamkke.domain.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestPart MultipartFile file
    ) {
        String imageUrl = imageUploader.upload(file, ImageDirectory.REFLECTION);

        return ResponseEntity.ok(imageUrl);
    }
}
