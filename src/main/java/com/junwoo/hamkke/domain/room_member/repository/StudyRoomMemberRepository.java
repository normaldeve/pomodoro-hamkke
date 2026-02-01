package com.junwoo.hamkke.domain.room_member.repository;

import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMemberEntity, Long> {

    List<StudyRoomMemberEntity> findByStudyRoomIdOrderByRoleAscCreatedAtAsc(Long studyRoomId);

    boolean existsByStudyRoomIdAndUserId(Long studyRoomId, Long userId);

    long countByStudyRoomId(Long studyRoomId);

    void deleteByStudyRoomIdAndUserId(Long roomId, Long userId);

    List<StudyRoomMemberEntity> findAllByStudyRoomId(Long roomId);

    boolean existsByUserId(Long userId);
}
