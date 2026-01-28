package com.junwoo.hamkke.domain.reflection.service;

import com.junwoo.hamkke.domain.image.ImageDirectory;
import com.junwoo.hamkke.domain.image.ImageUploader;
import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionRequest;
import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionResponse;
import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import com.junwoo.hamkke.domain.reflection.repository.ReflectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReflectionService {

    private final ReflectionRepository reflectionRepository;

    public CreateReflectionResponse createReflection(Long roomId, CreateReflectionRequest request) {

        ReflectionEntity reflection = ReflectionEntity.createReflection(roomId, request.userId(), request.sessionId(), request.imageUrl(), request.content(), request.focusScore());

        ReflectionEntity saved = reflectionRepository.save(reflection);

        return CreateReflectionResponse.from(saved);
    }
}
