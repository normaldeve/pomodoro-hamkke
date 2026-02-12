package com.junwoo.hamkke.domain.notification.service;

import com.google.firebase.messaging.*;
import com.junwoo.hamkke.domain.notification.dto.PushNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FCM 푸시 알림 전송 서비스
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {

    // 단일 디바이스에 푸시 알림을 전송
    public void sendToDevice(String token, PushNotificationRequest request) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(request.title())
                            .setBody(request.body())
                            .build())
                    .putAllData(request.data())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("[FcmPushService] 푸시 알림 전송 성공 - token: {}, response: {}",
                    maskToken(token), response);
        } catch (FirebaseMessagingException ex) {
            log.error("[FcmPushService] 푸시 알림 전송 실패 - token: {}, error: {}",
                    maskToken(token), ex.getMessage());

            if (ex.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                    ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                log.warn("[FcmPushService] 유효하지 않은 토큰 - token: {}", maskToken(token));
            }
        }
    }


     // 여러 디바이스에 푸시 알림 전송 (최대 500개)
    public void sendToDevices(List<String> tokens, PushNotificationRequest request) {
        if (tokens.isEmpty()) {
            log.warn("[FcmPushService] 전송할 토큰이 없습니다");
            return;
        }

        // FCM은 한 번에 최대 500개의 토큰을 처리할 수 있음
        int batchSize = 500;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> batch = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            sendBatch(batch, request);
        }
    }

    private void sendBatch(List<String> tokens, PushNotificationRequest request) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(request.title())
                            .setBody(request.body())
                            .build())
                    .putAllData(request.data())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            log.info("[FcmPushService] 배치 푸시 알림 전송 완료 - " + "성공: {}, 실패: {}, 총: {}",
                    response.getSuccessCount(),
                    response.getFailureCount(),
                    tokens.size());

            // 실패한 토큰 처리
            if (response.getFailureCount() > 0) {
                handleFailedTokens(tokens, response);
            }

        } catch (FirebaseMessagingException e) {
            log.error("[FcmPushService] 배치 푸시 알림 전송 실패", e);
        }
    }

    private void handleFailedTokens(List<String> tokens, BatchResponse response) {
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                String token = tokens.get(i);
                Exception exception = responses.get(i).getException();
                log.warn("[FcmPushService] 토큰 전송 실패 - token: {}, error: {}",
                        maskToken(token), exception.getMessage());
            }
        }
    }

    // 로그 토큰 비공개
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...";
    }
}
