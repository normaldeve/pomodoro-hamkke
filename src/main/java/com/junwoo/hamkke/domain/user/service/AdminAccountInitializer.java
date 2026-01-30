package com.junwoo.hamkke.domain.user.service;

import com.junwoo.hamkke.domain.user.entity.Role;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 30.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    @Value("${admin.id}")
    private String adminId;

    @Value("${admin.password}")
    private String adminPw;

    @Value("${admin.nickname:관리자}")
    private String adminNickname;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (adminId == null || adminId.isBlank() || adminPw == null || adminPw.isBlank()) {
            log.info("[INIT] ADMIN env 미설정 - 관리자 계정 생성 스킵");
            return;
        }
        if (userRepository.existsByUsername(adminId)) {
            log.info("[INIT] 관리자 계정 이미 존재 (id={})", adminId);
            return;
        }

        UserEntity admin = UserEntity.builder()
                .username(adminId)
                .password(passwordEncoder.encode(adminPw))
                .nickname(adminNickname)
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);


        log.info("[INIT] 관리자 계정 생성 완료 (id={})", adminId);
    }
}
