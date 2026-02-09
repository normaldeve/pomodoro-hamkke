package com.junwoo.hamkke.common.discord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordNotifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    public void sendError(String title, String message) {
        Map<String, Object> payload = Map.of(
                "content", "🚨 **" + title + "**\n" + message
        );

        try {
            restTemplate.postForEntity(webhookUrl, payload, String.class);
        } catch (Exception e) {
            log.error("Discord 알림 전송 실패", e);
        }
    }
}
