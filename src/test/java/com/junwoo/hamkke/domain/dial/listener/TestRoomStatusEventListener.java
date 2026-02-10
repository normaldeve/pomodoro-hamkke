package com.junwoo.hamkke.domain.dial.listener;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
@TestComponent
public class TestRoomStatusEventListener {

    private boolean shouldThrow;

    public void setShouldThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(TimerPhaseChangeEvent event) {
        if (shouldThrow) {
            throw new RuntimeException("테스트용 예외");
        }
    }
}
