package com.junwoo.hamkke.domain.dial.dto.event;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public record ReflectionCreateEvent(
        Long roomId,
        int sessionId
) {
}
