package com.junwoo.hamkke.domain.reflection.repository;

import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public interface ReflectionRepository extends JpaRepository<ReflectionEntity, Long> {


    @Query("""
    SELECT new com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse(
        r.id,
        r.sessionId,
        u.id,
        r.content,
        u.username,
        u.profileUrl,
        r.focusScore,
        r.imageUrl,
        r.createdAt
    )
    FROM ReflectionEntity r
    JOIN UserEntity u ON r.userId = u.id
    WHERE r.studyRoomId = :roomId
    ORDER BY r.createdAt ASC
    """)
    List<ReflectionResponse> findRoomReflections(Long roomId);
}
