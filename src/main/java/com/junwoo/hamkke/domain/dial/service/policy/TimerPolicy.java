package com.junwoo.hamkke.domain.dial.service.policy;

import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.dto.TimerState;

import java.util.UUID;

/**
 * 타이머 전략 인터 페이스
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
public interface TimerPolicy {

    TimerState initialize(UUID roomId, TimerStartRequest request);

    void onTick(TimerState state);

    void onPhaseFinished(TimerState state);

    boolean supportsPermanent();
}
