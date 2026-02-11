package com.junwoo.hamkke.domain.dial.schedule;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 11.
 */
@Component
public class TimerScheduler {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    private final Map<UUID, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public void schedule(UUID roomId, Runnable task) {

        cancel(roomId); // 기존 작업 제거

        ScheduledFuture<?> future =
                executor.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);

        tasks.put(roomId, future);
    }

    public void cancel(UUID roomId) {
        ScheduledFuture<?> future = tasks.remove(roomId);
        if (future != null) {
            future.cancel(false);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
