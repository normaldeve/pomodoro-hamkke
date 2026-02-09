package com.junwoo.hamkke.domain.dial.dto.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public record ReflectionCreateEvent(
        UUID roomId,
        int sessionId
) {
}
