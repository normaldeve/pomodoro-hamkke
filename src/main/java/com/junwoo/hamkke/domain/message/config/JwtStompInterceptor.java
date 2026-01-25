package com.junwoo.hamkke.domain.message.config;

import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
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

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);

        // CONNECT 프레임에서만 인증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader =
                    accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("[WebSocket] Authorization 헤더 없음");
                throw new IllegalArgumentException("Authorization header missing");
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("[WebSocket] JWT 검증 실패");
                throw new IllegalArgumentException("Invalid JWT token");
            }

            Long userId = jwtTokenProvider.getClaims(token).get("id", Long.class);
            String role = jwtTokenProvider.getClaims(token).get("role", String.class);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            // WebSocket 세션에 인증 정보 저장
            accessor.setUser(authentication);

            log.info("[WebSocket] CONNECT 인증 성공 - userId={}", userId);
        }

        return message;
    }
}
