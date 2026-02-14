package com.junwoo.hamkke.domain.notification.dto;

import java.util.Map;

/**
 * 푸시 알림 타입 및 메시지 템플릿
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
public enum NotificationType {

    FOCUS_STARTED(
            "집중 시간 시작",
            "🎯",
            "{roomTitle} - {session}회차 집중 시간이 시작되었습니다 ({focusMinutes}분)"
    ),

    BREAK_STARTED(
            "휴식 시간 시작",
            "☕",
            "{roomTitle} - 휴식 시간입니다! ({breakMinutes}분)"
    ),

    SESSION_FINISHED(
            "세션 종료",
            "✅",
            "{roomTitle} - {session}회차가 종료되었습니다"
    ),

    TOTAL_SESSION_FINISHED(
            "모든 세션 종료",
            "🎉",
            "{roomTitle} - 모든 세션을 완료했습니다! 수고하셨습니다 🎉"
    ),

    PLAN_START_REMINDER(
            "일정 시작 10분 전",
            "⏰",
            "\"{planTitle}\" 일정이 10분 뒤({startTime}) 시작됩니다"
    );

    private final String description;
    private final String emoji;
    private final String bodyTemplate;

    NotificationType(String description, String emoji, String bodyTemplate) {
        this.description = description;
        this.emoji = emoji;
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * 알림 제목 생성
     */
    public String getTitle() {
        return emoji + " " + description;
    }

    /**
     * 알림 본문 생성 (템플릿 + 변수 치환)
     */
    public String getBody(Map<String, String> variables) {
        String body = bodyTemplate;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            body = body.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return body;
    }

    /**
     * 푸시 알림 요청 생성
     */
    public PushNotificationDTO createRequest(Map<String, String> variables, Map<String, String> data) {
        return PushNotificationDTO.of(getTitle(), getBody(variables), data);
    }
}
