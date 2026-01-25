package com.junwoo.hamkke.domain.message.repository;

import com.junwoo.hamkke.domain.message.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
