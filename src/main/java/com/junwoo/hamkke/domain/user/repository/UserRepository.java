package com.junwoo.hamkke.domain.user.repository;

import com.junwoo.hamkke.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByNickname(String nickname);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.id <> :myId
          AND (
              :keyword IS NULL
              OR :keyword = ''
              OR u.nickname LIKE %:keyword%
          )
        ORDER BY u.nickname ASC
    """)
    List<UserEntity> searchUsers(
            @Param("myId") Long myId,
            @Param("keyword") String keyword
    );
}
