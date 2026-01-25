package com.junwoo.hamkke.domain.message.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.message.dto.MessageResponse;
import com.junwoo.hamkke.domain.message.dto.SendMessageRequest;
import com.junwoo.hamkke.domain.message.entity.MessageEntity;
import com.junwoo.hamkke.domain.message.repository.MessageRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.exception.UserException;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageResponse sendMessage(Long roomId, SendMessageRequest request, Long senderId) {

        MessageEntity message = MessageEntity.createMessage(roomId, senderId, request.content());

        MessageEntity savedMessage = messageRepository.save(message);

        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserException(ErrorCode.CANNOT_FOUND_USER));

        return MessageResponse.from(savedMessage, sender);
    }
}
