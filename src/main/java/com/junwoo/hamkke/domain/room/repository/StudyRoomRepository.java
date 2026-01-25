package com.junwoo.hamkke.domain.room.repository;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public interface StudyRoomRepository extends JpaRepository<StudyRoomEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from StudyRoomEntity r where r.id = :roomId")
    Optional<StudyRoomEntity> findByIdForUpdate(@Param("roomId") Long roomId);

    Page<StudyRoomEntity> findByStatusNot(RoomStatus status, Pageable pageable);
}
