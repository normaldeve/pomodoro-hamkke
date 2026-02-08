package com.junwoo.hamkke.domain.reflection.repository;

import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
        u.nickname,
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

    // 특정 날짜의 회고 조회
    @Query("""
    SELECT r
    FROM ReflectionEntity r
    WHERE r.userId = :userId
    AND r.createdAt >= :startOfDay
    AND r.createdAt < :endOfDay
    ORDER BY r.createdAt DESC
    """)
    List<ReflectionEntity> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // 특정 월의 회고 조회
    @Query("""
    SELECT r
    FROM ReflectionEntity r
    WHERE r.userId = :userId
    AND YEAR(r.createdAt) = :year
    AND MONTH(r.createdAt) = :month
    ORDER BY r.createdAt DESC
    """)
    List<ReflectionEntity> findByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    // 전체 회고 조회
    @Query("""
    SELECT r
    FROM ReflectionEntity r
    WHERE r.userId = :userId
    ORDER BY r.createdAt DESC
    """)
    List<ReflectionEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
