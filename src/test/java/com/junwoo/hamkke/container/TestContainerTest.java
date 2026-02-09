package com.junwoo.hamkke.container;

import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

class TestContainerTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void save_user_test() {
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();

        UserEntity save = userRepository.save(user);

        assertThat(save.getUsername(), equalTo("test"));
    }
}