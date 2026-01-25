package com.junwoo.hamkke.domain.dial.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Data
public class DialDragMessage {
    private DialDragType type;
    private Long roomId;
    private Integer minutes; // 1~60
    private LocalDateTime timestamp;

    public DialDragMessage() {
        this.timestamp = LocalDateTime.now();
    }
}
