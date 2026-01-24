package com.junwoo.hamkke.domain.user.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.image.ImageDirectory;
import com.junwoo.hamkke.domain.image.ImageUploader;
import com.junwoo.hamkke.domain.user.dto.SignupRequest;
import com.junwoo.hamkke.domain.user.dto.SignupResponse;
import com.junwoo.hamkke.domain.user.dto.UserInfo;
import com.junwoo.hamkke.domain.user.dto.UserSearchResponse;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.exception.UserException;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploader imageUploader;

    public SignupResponse signup(SignupRequest request) {

        userValidator.checkNicknameDuplicate(request.nickname());

        UserEntity user = UserEntity.createUser(request, passwordEncoder);

        UserEntity savedUser = userRepository.save(user);

        log.info("[UserService] 회원 가입 성공 - nickname: {}", savedUser.getNickname());

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getNickname(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    public String updateProfile(Long userId, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.CANNOT_FOUND_USER));

        String newImageUrl = imageUploader.upload(file, ImageDirectory.PROFILE);

        if (user.getProfileUrl() != null && !user.getProfileUrl().isEmpty()) {
            try {
                imageUploader.delete(user.getProfileUrl());
            } catch (Exception e) {
                log.error("[UserService] 사용자 프로필 삭제 실패 - url: {} - error: {}", user.getProfileUrl(), e.getMessage(), e);
            }
        }

        user.updateProfile(newImageUrl);

        log.info("[UserService] 프로필 이미지 변경 - userId: {}, imageUrl: {}", userId, newImageUrl);

        return newImageUrl;
    }

    @Transactional(readOnly = true)
    public AuthDTO findByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.CANNOT_FOUND_USER));

        return AuthDTO.from(userEntity);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse> searchUsers(Long myId, String keyword) {
        return userRepository.searchUsers(myId, keyword).stream()
                .map(UserSearchResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserInfo getUserInfo(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.CANNOT_FOUND_USER));

        return UserInfo.from(user);
    }
}
