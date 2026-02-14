package com.junwoo.hamkke.domain.dial.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public record SessionFinishedEvent(
        UUID roomId
) {}
