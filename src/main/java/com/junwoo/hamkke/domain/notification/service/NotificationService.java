package com.junwoo.hamkke.domain.notification.service;

import com.junwoo.hamkke.domain.notification.dto.PushNotificationRequest;
import com.junwoo.hamkke.domain.notification.entity.DeviceTokenEntity;
import com.junwoo.hamkke.domain.notification.entity.DeviceType;
import com.junwoo.hamkke.domain.notification.repository.DeviceTokenRepository;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final FcmPushService fcmPushService;
    private final DeviceTokenRepository deviceTokenRepository;
    private final StudyRoomMemberRepository memberRepository;

    // 방에 있는 모든 사용자에게 푸시 알림을 전송합니다
    public void sendToRoomMembers(UUID roomId, PushNotificationRequest request) {
        List<StudyRoomMemberEntity> members = memberRepository.findByStudyRoomIdOrderByRoleAscCreatedAtAsc(roomId);

        if (members.isEmpty()) {
            log.info("[NotificationService] 방에 사용자들이 없습니다 - roomId: {}", roomId);
            return;
        }

        List<Long> userIds = members.stream()
                .map(StudyRoomMemberEntity::getUserId)
                .toList();

        List<DeviceTokenEntity> tokens = deviceTokenRepository.findActiveTokensByUserIds(userIds);

        if (tokens.isEmpty()) {
            log.info("[NotificationService] 활성화 된 토큰이 없습니다 - roomId: {}", roomId);
            return;
        }

        List<String> fcmTokens = tokens.stream()
                .map(DeviceTokenEntity::getFcmToken)
                .toList();

        log.info("[NotificationService] 푸시 알림 전송 시작 - roomId: {}, 대상 사용자 : {}명", roomId, userIds.size());

        fcmPushService.sendToDevices(fcmTokens, request);
    }

    // 특정 사용자에게 푸시 알림을 전송
    public void sendToUser(Long userId, PushNotificationRequest request) {
        List<DeviceTokenEntity> tokens = deviceTokenRepository.findActiveTokensByUserId(userId);

        if (tokens.isEmpty()) {
            log.info("[NotificationService] 활성화 된 토큰이 없습니다 - request: {}", request);
            return;
        }

        for (DeviceTokenEntity token : tokens) {
            fcmPushService.sendToDevice(token.getFcmToken(), request);
        }
    }

    // 디바이스 토큰 등록 및 갱신
    public void registerToken(Long userId, String fcmToken, DeviceType deviceType) {
        log.info("[NotificationService] 토큰 등록 요청 확인 - userId: {}, deviceType: {}", userId, deviceType);
        deviceTokenRepository.findByFcmToken(fcmToken)
                .ifPresentOrElse(
                        existingToken -> {
                            existingToken.activate();
                            log.info("[NotificationService] 기존 토큰을 활성화 - userId: {}, deviceType: {}", userId, deviceType);
                        }, () -> {
                            DeviceTokenEntity newToken = DeviceTokenEntity.create(userId, fcmToken, deviceType);
                            deviceTokenRepository.save(newToken);
                            log.info("[NotificationService] 새 토큰 등록 - userId: {}, deviceType: {}", userId, deviceType);
                        }
                );
    }

    // 디바이스 토큰 비활성화
    public void deactivateToken(String fcmToken) {
        deviceTokenRepository.findByFcmToken(fcmToken)
                .ifPresent(token -> {
                    token.deactivate();
                    log.info("[NotificationService] 토큰 비활성화 - token: {}...", fcmToken.substring(0, 10));
                });
    }
}
