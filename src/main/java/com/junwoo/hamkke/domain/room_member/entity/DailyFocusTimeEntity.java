package com.junwoo.hamkke.domain.room_member.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.time.LocalDate;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
@Getter
@Builder
@Entity
@Table(
        name = "daily_focus_time",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "focus_date"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DailyFocusTimeEntity extends BaseEntity {

    private Long userId;

    private LocalDate focusDate;

    private int totalFocusMinutes;

    public void addMinutes(int minutes) {
        this.totalFocusMinutes += minutes;
    }
}
