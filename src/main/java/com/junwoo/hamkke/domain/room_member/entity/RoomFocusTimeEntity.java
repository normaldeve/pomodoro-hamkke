package com.junwoo.hamkke.domain.room_member.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 방에서 사용자 별 학습 시간을 기록하는 엔티티
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
@Entity
@Table(
        name = "room_focus_time",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "user_id",
                        "study_room_id",
                        "focus_date"
                })
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoomFocusTimeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long userId;

    private UUID studyRoomId;

    private LocalDate focusDate;

    private int totalFocusMinutes;

    public void addMinutes(int minutes) {
        this.totalFocusMinutes += minutes;
    }
}
