package com.junwoo.hamkke.domain.room_member.dto;

import lombok.Getter;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Getter
public class MemberFocusRuntime {

    private boolean focusing;
    private long focusStartedAt;
    private int totalFocusedSeconds;

    public void startFocus() {
        if (focusing) return;
        focusing = true;
        focusStartedAt = System.currentTimeMillis();
    }

    public void stopFocus() {
        if (!focusing) return;

        long now = System.currentTimeMillis();
        totalFocusedSeconds += (int) ((now - focusStartedAt) / 1000);

        focusing = false;
        focusStartedAt = 0;
    }

    public int finishAndGetTotalSeconds() {
        stopFocus();
        return totalFocusedSeconds;
    }

    public void resetForNextSession() {
        totalFocusedSeconds = 0;
        focusing = false;
        focusStartedAt = 0;
    }
}
