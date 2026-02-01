package com.junwoo.hamkke.domain.point.repository;

import com.junwoo.hamkke.domain.point.entity.PointLogEntity;
import com.junwoo.hamkke.domain.point.entity.PointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
public interface PointLogRepository extends JpaRepository<PointLogEntity, Long> {

    // 특정 타입과 refId로 중복 확인 (유니크 제약조건 활용)
    boolean existsByTypeAndRefId(PointType type, Long refId);

    // 사용자의 총 포인트 조회
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PointLogEntity p WHERE p.userId = :userId")
    int getTotalPoints(@Param("userId") Long userId);

    // 사용자의 포인트 히스토리 조회 (최근순)
    List<PointLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 기간 동안의 포인트 조회
    @Query("SELECT p FROM PointLogEntity p WHERE p.userId = :userId AND p.createdAt BETWEEN :start AND :end ORDER BY p.createdAt DESC")
    List<PointLogEntity> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
