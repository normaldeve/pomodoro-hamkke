package com.junwoo.hamkke.domain.reflection.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionRequest;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import com.junwoo.hamkke.domain.reflection.repository.ReflectionRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.exception.UserException;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReflectionService {

    private final UserRepository userRepository;
    private final ReflectionRepository reflectionRepository;

    public ReflectionResponse createReflection(Long roomId, CreateReflectionRequest request) {

        ReflectionEntity reflection = ReflectionEntity.createReflection(roomId, request.userId(), request.sessionId(), request.imageUrl(), request.content(), request.focusScore());

        ReflectionEntity saved = reflectionRepository.save(reflection);

        UserEntity user = userRepository.findById((request.userId()))
                .orElseThrow(() -> new UserException(ErrorCode.CANNOT_FOUND_USER));

        return ReflectionResponse.builder()
                .reflectionId(saved.getId())
                .sessionId(saved.getSessionId())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .nickname(user.getNickname())
                .userProfileUrl(user.getProfileUrl())
                .focusScore(saved.getFocusScore())
                .imageUrl(saved.getImageUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReflectionResponse> getRoomReflections(Long roomId) {

        return reflectionRepository.findRoomReflections(roomId);
    }
}
