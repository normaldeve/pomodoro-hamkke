package com.junwoo.hamkke.domain.room_member.event;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.event.TimerPhaseChangeEvent;
import com.junwoo.hamkke.domain.room_member.service.MemberFocusRuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Component
@RequiredArgsConstructor
public class MemberFocusTimerEventListener {

    private final MemberFocusRuntimeService runtimeService;

    @EventListener
    public void handle(TimerPhaseChangeEvent event) {
        if (event.phase() == TimerPhase.FOCUS) {
            runtimeService.startFocus(event.roomId());
        }

        if (event.phase() == TimerPhase.BREAK || event.phase() == TimerPhase.FINISHED) {
            runtimeService.stopFocus(event.roomId());
        }
    }
}