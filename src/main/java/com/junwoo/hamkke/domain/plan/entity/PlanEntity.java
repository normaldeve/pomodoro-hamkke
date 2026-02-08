package com.junwoo.hamkke.domain.plan.entity;

import com.junwoo.hamkke.common.entity.UpdatableBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
@Entity
@Table(
        name = "plan",
        indexes = {
                @Index(name = "idx_plan_user_date", columnList = "user_id, plan_date")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanEntity extends UpdatableBaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventColor color;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    public void updatePlan(String title, LocalDate planDate, LocalTime startTime, LocalTime endTime, EventColor color) {
        this.title = title;
        this.planDate = planDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }

    public void complete() {
        this.completed = true;
    }
}
