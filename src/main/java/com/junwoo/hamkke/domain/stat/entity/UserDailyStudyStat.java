package com.junwoo.hamkke.domain.stat.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.time.LocalDate;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Entity
@Getter
@Builder
@Table(
        name = "user_daily_study_stat",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "study_date"})
        },
        indexes = {
                @Index(name = "idx_user_date", columnList = "user_id, study_date")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserDailyStudyStat extends BaseEntity {

    private Long userId;

    private LocalDate studyDate;

    private int totalMinutes;

    private int level;

    public static UserDailyStudyStat create(Long userId, LocalDate studyDate) {
        return UserDailyStudyStat.builder()
                .userId(userId)
                .studyDate(studyDate)
                .build();
    }

    public void addMinutes(int minutes) {
        this.totalMinutes += minutes;
        recalcLevel();
    }

    private void recalcLevel() {
        if (totalMinutes == 0) {
            level = 0;
        } else if (totalMinutes <= 60) {
            level = 1;
        } else if (totalMinutes <= 120) {
            level = 2;
        } else if (totalMinutes <= 180) {
            level = 3;
        } else {
            level = 4;
        }
    }
}