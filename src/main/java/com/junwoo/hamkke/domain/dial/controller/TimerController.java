package com.junwoo.hamkke.domain.dial.controller;

import com.junwoo.hamkke.domain.dial.dto.NextFocusTimeUpdateRequest;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Controller
@RequiredArgsConstructor
public class TimerController {

    private final TimerStateService timerStateService;

    @MessageMapping("/study-room/{roomId}/timer/start")
    public void startTimer(
            @DestinationVariable Long roomId,
            @Payload TimerStartRequest request
    ) {

        timerStateService.start(roomId, request);
    }

    @MessageMapping("/study-room/{roomId}/timer/pause")
    public void pauseTimer(
            @DestinationVariable Long roomId
    ) {
        timerStateService.pause(roomId);
    }

    @MessageMapping("/study-room/{roomId}/timer/resume")
    public void resumeTimer(
            @DestinationVariable Long roomId
    ) {
        timerStateService.resume(roomId);
    }

    @MessageMapping("/study-room/{roomId}/timer/next-focus")
    public void updateNextFocusTime(
            @DestinationVariable Long roomId,
            @Payload NextFocusTimeUpdateRequest request
    ) {

        timerStateService.updateNextFocusTime(roomId, request.focusMinutes());
    }
}
