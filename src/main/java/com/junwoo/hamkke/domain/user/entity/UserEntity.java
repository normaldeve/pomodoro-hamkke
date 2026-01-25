package com.junwoo.hamkke.domain.user.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import com.junwoo.hamkke.domain.user.dto.SignupRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Getter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    private String username;

    private String nickname;

    private String password;

    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    public  static UserEntity createUser(SignupRequest request, PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
                .username(request.username())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
    }

    public void updateProfile(String newProfileUrl) {
        this.profileUrl = newProfileUrl;
    }
}
