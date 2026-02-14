package com.junwoo.hamkke.domain.notification.dto;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
public record PushNotificationDTO(
        String title,
        String body,
        Map<String, String> data
) {
    public static PushNotificationDTO of(String title, String body) {
        return new PushNotificationDTO(title, body, new HashMap<>());
    }

    public static PushNotificationDTO of(String title, String body, Map<String, String> data) {
        return new PushNotificationDTO(title, body, data);
    }
}
