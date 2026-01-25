package com.junwoo.hamkke.common.websocket;

import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtStompInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 프레임에서만 인증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("[WebSocket] Authorization 헤더 없음");
                throw new IllegalArgumentException("Authorization header missing");
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("[WebSocket] JWT 검증 실패");
                throw new IllegalArgumentException("Invalid JWT token");
            }

            // ===== JWT Claims 추출 =====
            Long userId = jwtTokenProvider.getClaims(token).get("id", Long.class);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String nickname = jwtTokenProvider.getClaims(token).get("nickname", String.class);
            String profileUrl = jwtTokenProvider.getClaims(token).get("profileUrl", String.class);
            Role role = Role.valueOf(
                    jwtTokenProvider.getClaims(token).get("role", String.class)
            );

            // ===== AuthDTO 생성 =====
            AuthDTO authDTO = AuthDTO.builder()
                    .id(userId)
                    .username(username)
                    .nickname(nickname)
                    .profileUrl(profileUrl)
                    .role(role)
                    .build();

            // ===== CustomUserDetails 생성 (핵심) =====
            CustomUserDetails userDetails =
                    new CustomUserDetails(authDTO, null);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,               // principal
                            null,
                            userDetails.getAuthorities()
                    );

            // WebSocket 세션에 인증 정보 저장
            accessor.setUser(authentication);

            log.info("[WebSocket] CONNECT 인증 성공 - userId={}, role={}", userId, role);
        }

        return message;
    }
}