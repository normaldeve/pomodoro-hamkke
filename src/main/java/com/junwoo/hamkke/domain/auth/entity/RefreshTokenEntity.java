package com.junwoo.hamkke.domain.auth.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import com.junwoo.hamkke.common.entity.UpdatableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_expire_at", columnList = "expireAt")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshTokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }

    public void updateToken(String newToken, LocalDateTime newExpireAt) {
        this.token = newToken;
        this.expireAt = newExpireAt;
    }
}
