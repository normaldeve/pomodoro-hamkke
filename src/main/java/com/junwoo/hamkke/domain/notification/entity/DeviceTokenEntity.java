package com.junwoo.hamkke.domain.notification.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 디바이스 FCM 토큰 관리
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@Getter
@Entity
@Builder
@Table(name = "device_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeviceTokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String fcmToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    private boolean active;

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public static DeviceTokenEntity create(Long userId, String fcmToken, DeviceType deviceType) {
        return DeviceTokenEntity.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .deviceType(deviceType)
                .active(true)
                .build();
    }
}
