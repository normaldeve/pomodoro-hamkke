package com.junwoo.hamkke.domain.room.repository;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public interface StudyRoomRepository extends JpaRepository<StudyRoomEntity, UUID> {

    // 일반 방 조회 (상시 운영 방 제외)
    Page<StudyRoomEntity> findByStatusNotAndPermanentFalse(RoomStatus status, Pageable pageable);

    // 상시 운영 방 조회
    List<StudyRoomEntity> findByPermanentTrue();

    // 상시 운영 방 존재 여부
    boolean existsByPermanentTrue();

    @Modifying
    @Query("""
    update StudyRoomEntity r
    set r.currentParticipants = r.currentParticipants + 1
    where r.id = :roomId
    and r.currentParticipants < r.maxParticipants
""")
    int increaseIfNotFull(UUID roomId);
}
