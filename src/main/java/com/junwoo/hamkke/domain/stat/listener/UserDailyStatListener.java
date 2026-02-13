package com.junwoo.hamkke.domain.stat.listener;

import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Slf4j
@Component
public class UserDailyStatListener {

    @EventListener
    public void onFocusPhaseEnd(FocusTimeFinishedEvent event) {
        // 사용자별 부분 정산(중간 입장/퇴장 포함)은 FocusTimeTrackingListener + FocusTimeService에서 처리합니다.
        log.debug("[UserDailyStatListener] migrated - roomId: {}, currentSession: {}",
                event.roomId(), event.currentSessionId());
    }
}
