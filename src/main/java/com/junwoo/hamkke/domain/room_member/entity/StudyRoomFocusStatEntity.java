package com.junwoo.hamkke.domain.room_member.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Getter
@Builder
@Entity
@Table(name = "study_room_focus_stat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyRoomFocusStatEntity extends BaseEntity {

    private Long roomId;

    private Long userId;

    private int focusSeconds;
}
