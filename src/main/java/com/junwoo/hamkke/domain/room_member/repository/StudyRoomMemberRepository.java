package com.junwoo.hamkke.domain.room_member.repository;

import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMemberEntity, Long> {

    List<StudyRoomMemberEntity> findByStudyRoomIdOrderByRoleAscCreatedAtAsc(UUID studyRoomId);

    void deleteByStudyRoomIdAndUserId(UUID roomId, Long userId);

    List<StudyRoomMemberEntity> findAllByStudyRoomId(UUID roomId);

    boolean existsByUserId(Long userId);

    long countByStudyRoomId(UUID studyRoomId);

    Optional<StudyRoomMemberEntity> findByStudyRoomIdAndUserId(UUID studyRoomId, Long userId);

    @Query("""
                        SELECT m FROM StudyRoomMemberEntity  m
                        WHERE m.studyRoomId = :roomId
                        AND m.role = 'MEMBER'
                        ORDER BY m.createdAt ASC
                        LIMIT 1
            """)
    Optional<StudyRoomMemberEntity> findOldestMember(@Param("roomId") UUID roomId);

    Optional<StudyRoomMemberEntity> findByUserId(Long userId);
}