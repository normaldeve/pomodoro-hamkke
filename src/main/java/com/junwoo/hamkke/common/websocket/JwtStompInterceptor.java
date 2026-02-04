// JwtStompInterceptor.java
package com.junwoo.hamkke.common.websocket;

import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * WebSocket STOMP 연결 시 JWT 인증 처리
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtStompInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class
        );

        // CONNECT 프레임에서만 인증
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            log.debug("[WebSocket] CONNECT 요청 감지");

            try {
                authenticateUser(accessor);
            } catch (Exception e) {
                log.error("[WebSocket] 인증 실패: {}", e.getMessage());
                // 인증 실패 시에도 연결은 허용하되, Principal은 null로 유지
                // 또는 예외를 던져서 연결 자체를 거부할 수 있음
                // throw new IllegalArgumentException("WebSocket authentication failed: " + e.getMessage());
            }
        }

        return message;
    }

    /**
     * JWT 토큰으로부터 사용자 인증 처리
     */
    private void authenticateUser(StompHeaderAccessor accessor) {

        // Authorization 헤더 추출
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[WebSocket] Authorization 헤더 없음 또는 형식 오류");
            return; // 인증 없이 진행 (또는 예외 던지기)
        }

        String token = authHeader.substring(7);

        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("[WebSocket] JWT 토큰 검증 실패");
            return; // 인증 없이 진행 (또는 예외 던지기)
        }

        // JWT Claims 추출
        Claims claims = jwtTokenProvider.getClaims(token);

        Long userId = claims.get("id", Long.class);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        String nickname = claims.getSubject(); // subject에 nickname이 저장됨
        String roleStr = claims.get("role", String.class);

        if (userId == null || roleStr == null) {
            log.warn("[WebSocket] JWT Claims에 필수 정보 누락 - userId: {}, role: {}", userId, roleStr);
            return;
        }

        Role role = Role.valueOf(roleStr);

        // AuthDTO 생성 (profileUrl은 토큰에 없으므로 null)
        AuthDTO authDTO = AuthDTO.builder()
                .id(userId)
                .username(username)
                .nickname(nickname)
                .profileUrl(null) // 토큰에 포함되지 않음
                .role(role)
                .build();

        // CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(authDTO, null);

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // WebSocket 세션에 인증 정보 저장
        accessor.setUser(authentication);

        log.info("[WebSocket] CONNECT 인증 성공 - userId: {}, nickname: {}, role: {}",
                userId, nickname, role);
    }
}