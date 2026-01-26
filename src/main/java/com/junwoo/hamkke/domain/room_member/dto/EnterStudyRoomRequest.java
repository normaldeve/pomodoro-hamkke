package com.junwoo.hamkke.domain.room_member.dto;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
public record EnterStudyRoomRequest(
        Long userId,
        String password
) {
}
