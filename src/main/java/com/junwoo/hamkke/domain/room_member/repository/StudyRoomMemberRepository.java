package com.junwoo.hamkke.domain.room_member.repository;

import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMemberEntity, Long> {

    List<StudyRoomMemberEntity> findByStudyRoomIdOrderByRoleAscCreatedAtAsc(Long studyRoomId);

    boolean existsByStudyRoomIdAndUserId(Long studyRoomId, Long userId);

    void deleteByStudyRoomIdAndUserId(Long roomId, Long userId);

    List<StudyRoomMemberEntity> findAllByStudyRoomId(Long roomId);

    boolean existsByUserId(Long userId);

    long countByStudyRoomId(Long studyRoomId);

    Optional<StudyRoomMemberEntity> findByStudyRoomIdAndUserId(Long studyRoomId, Long userId);

    @Query("""
                        SELECT m FROM StudyRoomMemberEntity  m
                        WHERE m.studyRoomId = :roomId
                        AND m.role = 'MEMBER'
                        ORDER BY m.createdAt ASC
                        LIMIT 1
            """)
    Optional<StudyRoomMemberEntity> findOldestMember(@Param("roomId") Long roomId);

    Optional<StudyRoomMemberEntity> findByUserId(Long userId);
}