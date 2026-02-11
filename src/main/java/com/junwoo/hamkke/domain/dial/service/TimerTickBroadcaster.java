package com.junwoo.hamkke.domain.dial.service;

import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.TimerTickMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 11.
 */
@Component
@RequiredArgsConstructor
public class TimerTickBroadcaster {

    private final SimpMessagingTemplate template;

    public void sendTick(TimerTickMessage tick) {
        template.convertAndSend(WebSocketDestination.tick(tick.roomId()), tick);
    }
}
