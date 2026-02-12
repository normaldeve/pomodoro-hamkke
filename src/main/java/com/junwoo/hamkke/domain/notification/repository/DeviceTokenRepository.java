package com.junwoo.hamkke.domain.notification.repository;

import com.junwoo.hamkke.domain.notification.entity.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
public interface DeviceTokenRepository extends JpaRepository<DeviceTokenEntity, Long> {

    Optional<DeviceTokenEntity> findByFcmToken(String fcmToken);

    @Query("select d from DeviceTokenEntity d where d.userId = :userId and d.active = true")
    List<DeviceTokenEntity> findActiveTokensByUserId(@Param("userId") Long userId);

    @Query("select d from DeviceTokenEntity d where d.userId in :userIds and d.active = true")
    List<DeviceTokenEntity> findActiveTokensByUserIds(@Param("userIds") List<Long> userIds);
}
