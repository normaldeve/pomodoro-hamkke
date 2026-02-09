package com.junwoo.hamkke.domain.message.controller;

import com.junwoo.hamkke.domain.message.dto.MessageResponse;
import com.junwoo.hamkke.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageAPIController {

    private final MessageService messageService;

    @GetMapping("/{roomId}")
    public ResponseEntity<Slice<MessageResponse>> getMessages(
            @PathVariable UUID roomId,
            @RequestParam(required = false) Long lastMessageId,
            @RequestParam(defaultValue = "30") int size
    ) {

        return ResponseEntity.ok(messageService.getMessages(roomId, lastMessageId, size));
    }
}
