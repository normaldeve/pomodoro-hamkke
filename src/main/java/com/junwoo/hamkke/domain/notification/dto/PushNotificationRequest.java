package com.junwoo.hamkke.domain.notification.dto;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
public record PushNotificationRequest(
        String title,
        String body,
        Map<String, String> data
) {
    public static PushNotificationRequest of(String title, String body) {
        return new PushNotificationRequest(title, body, new HashMap<>());
    }

    public static PushNotificationRequest of(String title, String body, Map<String, String> data) {
        return new PushNotificationRequest(title, body, data);
    }
}
