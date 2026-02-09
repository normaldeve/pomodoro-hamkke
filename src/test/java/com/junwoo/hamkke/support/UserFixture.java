package com.junwoo.hamkke.support;

import com.junwoo.hamkke.domain.user.entity.UserEntity;

/**
 * 테스트를 위한 사용자 생성 클래스
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
public class UserFixture {

    // Username으로만 사용자 생성
    public static UserEntity create(String username) {
        return UserEntity.builder()
                .username(username)
                .build();
    }
}
