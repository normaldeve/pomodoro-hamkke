package com.junwoo.hamkke.domain.point.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
@Builder
@Entity
@Table(
        name = "point_log",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_point_type_ref",
                        columnNames = {"type", "ref_id"}
                )
        },
        indexes = {
                @Index(name = "idx_point_user", columnList = "user_id"),
                @Index(name = "idx_point_created", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointLogEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointType type;

    @Column(nullable = false)
    private int amount;

    // 행동마다 들어가는 id가 다르다.
    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
