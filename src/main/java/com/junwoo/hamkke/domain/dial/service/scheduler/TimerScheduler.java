package com.junwoo.hamkke.domain.dial.service.scheduler;

import com.junwoo.hamkke.domain.dial.dto.TimerState;
import com.junwoo.hamkke.domain.dial.service.policy.TimerPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Component
public class TimerScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    private final Map<UUID, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public void start(
            UUID roomId,
            TimerState state,
            TimerPolicy policy,
            Runnable broadcaster
    ) {
        stop(roomId);

        ScheduledFuture<?> task =
                scheduler.scheduleAtFixedRate(() -> {
                    if (!state.isRunning()) return;

                    policy.onTick(state);

                    if (state.getRemainingSeconds() <= 0) {
                        policy.onPhaseFinished(state);
                    }

                    broadcaster.run();
                }, 1, 1, TimeUnit.SECONDS);

        tasks.put(roomId, task);
    }

    public void stop(UUID roomId) {
        ScheduledFuture<?> task = tasks.remove(roomId);
        if (task != null) {
            task.cancel(false);
        }
    }
}