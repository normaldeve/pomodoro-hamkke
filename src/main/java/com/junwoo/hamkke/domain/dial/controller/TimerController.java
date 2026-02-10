package com.junwoo.hamkke.domain.dial.controller;

import com.junwoo.hamkke.domain.dial.dto.NextFocusTimeUpdateRequest;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class TimerController {

    private final TimerStateService timerStateService;

    @MessageMapping("/study-room/{roomId}/timer/start")
    public void startTimer(
            @DestinationVariable UUID roomId,
            @Payload TimerStartRequest request
    ) {
        timerStateService.start(roomId, request);
    }

    @MessageMapping("/study-room/{roomId}/timer/pause")
    public void pauseTimer(
            @DestinationVariable UUID roomId
    ) {
        timerStateService.pause(roomId);
    }

    @MessageMapping("/study-room/{roomId}/timer/resume")
    public void resumeTimer(
            @DestinationVariable UUID roomId
    ) {
        timerStateService.resume(roomId);
    }

    @MessageMapping("/study-room/{roomId}/timer/next-focus")
    public void updateNextFocusTime(
            @DestinationVariable UUID roomId,
            @Payload NextFocusTimeUpdateRequest request
    ) {

        timerStateService.updateNextFocusTime(roomId, request.focusMinutes());
    }
}
