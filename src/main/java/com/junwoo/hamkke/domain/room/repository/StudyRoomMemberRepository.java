package com.junwoo.hamkke.domain.room.repository;

import com.junwoo.hamkke.domain.room.entity.StudyRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMemberEntity, Long> {
}
