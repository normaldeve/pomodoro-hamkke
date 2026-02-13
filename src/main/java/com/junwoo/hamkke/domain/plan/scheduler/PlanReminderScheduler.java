package com.junwoo.hamkke.domain.plan.scheduler;

import com.junwoo.hamkke.domain.notification.dto.NotificationType;
import com.junwoo.hamkke.domain.notification.dto.PushNotificationRequest;
import com.junwoo.hamkke.domain.notification.service.NotificationService;
import com.junwoo.hamkke.domain.plan.entity.PlanEntity;
import com.junwoo.hamkke.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 일정 시작 10분 전 리마인더 푸시 전송 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlanReminderScheduler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final PlanRepository planRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendPlanStartReminders() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        LocalDateTime reminderWindowStart = now.plusMinutes(10);
        LocalDateTime reminderWindowEnd = reminderWindowStart.plusMinutes(1);

        List<PlanEntity> targets = planRepository.findReminderTargets(
                reminderWindowStart.toLocalDate(),
                reminderWindowStart.toLocalTime(),
                reminderWindowEnd.toLocalTime()
        );

        if (targets.isEmpty()) {
            return;
        }

        log.info("[PlanReminderScheduler] 리마인더 전송 대상 {}건", targets.size());

        for (PlanEntity plan : targets) {
            PushNotificationRequest request = NotificationType.PLAN_START_REMINDER.createRequest(
                    Map.of(
                            "planTitle", plan.getTitle(),
                            "startTime", plan.getStartTime().format(TIME_FORMATTER)
                    ),
                    Map.of(
                            "type", NotificationType.PLAN_START_REMINDER.name(),
                            "planId", String.valueOf(plan.getId()),
                            "planDate", plan.getPlanDate().toString(),
                            "startTime", plan.getStartTime().format(TIME_FORMATTER)
                    )
            );

            notificationService.sendToUser(plan.getUserId(), request);
            plan.markReminderSent();
        }
    }
}
