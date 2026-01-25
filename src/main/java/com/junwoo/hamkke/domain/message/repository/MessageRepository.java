package com.junwoo.hamkke.domain.message.repository;

import com.junwoo.hamkke.domain.message.dto.MessageResponse;
import com.junwoo.hamkke.domain.message.entity.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("""
        select new com.junwoo.hamkke.domain.message.dto.MessageResponse(
            m.id,
            m.roomId,
            m.senderId,
            u.nickname,
            u.profileUrl,
            m.content,
            m.createdAt
        )
        from MessageEntity m
        join UserEntity u on u.id = m.senderId
        where m.roomId = :roomId
          and (:lastMessageId is null or m.id < :lastMessageId)
        order by m.id desc
    """)
    Slice<MessageResponse> findRoomMessages(
            @Param("roomId") Long roomId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );

}
