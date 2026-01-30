package com.junwoo.hamkke.domain.room.dto;

import com.junwoo.hamkke.domain.room.entity.TimerType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public record CreateStudyRoomRequest(

        @NotBlank(message = "스터디 방 이름을 입력해주세요 :)")
        String title,

        Set<String> hashtags,

        @Min(value = 1, message = "쉬는 시간은 1분보다 작을 수 없어요 :)") @Max(value = 15, message = "쉬는 시간은 15분이 최대에요 :)")
        int breakMinutes,

        @Min(value = 1, message = "전체 세션은 1회 이상 설정해야 해요 :)") @Max(value = 10, message = "전체 세션은 10회 이내여야 해요 :)")
        int totalSessions,

        @Min(value = 1, message = "최대 참여 인원은 1명 이상이어야 해요 :)") @Max(value = 10, message = "최대 참여 인원은 10명이 최대에요 :)")
        int maxParticipants,

        boolean secret,

        String password,

        TimerType timerType
) {

}
