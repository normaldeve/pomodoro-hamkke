package com.junwoo.hamkke.domain.user.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.user.exception.UserException;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void checkNicknameDuplicate(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            log.error("[UserValidator] 회원 가입 실패 - 이미 존재하는 닉네임: {}", nickname);

            throw new UserException(ErrorCode.ALREADY_EXISTS_NICKNAME);
        }
    }

    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserException(ErrorCode.CANNOT_FOUND_USER);
        }
    }
}
