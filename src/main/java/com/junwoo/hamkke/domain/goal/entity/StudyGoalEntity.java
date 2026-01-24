package com.junwoo.hamkke.domain.goal.entity;

import com.junwoo.hamkke.common.entity.UpdatableBaseEntity;
import com.junwoo.hamkke.domain.goal.dto.CreateStudyGoalRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Getter
@Entity
@Builder
@Table(name = "study_goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyGoalEntity extends UpdatableBaseEntity {

    private Long studyRoomId;

    private Long userId;

    private String content;

    private boolean completed;

    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    public static StudyGoalEntity createGoal(Long roomId, Long userId, CreateStudyGoalRequest request) {
        return StudyGoalEntity.builder()
                .studyRoomId(roomId)
                .userId(userId)
                .content(request.content())
                .completed(false)
                .build();
    }
}
