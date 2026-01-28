package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.domain.room_member.entity.RoomFocusTimeEntity;
import com.junwoo.hamkke.domain.room_member.repository.RoomFocusTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Service
@RequiredArgsConstructor
public class FocusTimeService {

    private final RoomFocusTimeRepository focusTimeRepository;

    public int getTodayRoomFocusTime(Long userId, Long roomId) {
        RoomFocusTimeEntity focusTime = focusTimeRepository.findByUserIdAndStudyRoomIdAndFocusDate(userId, roomId, LocalDate.now())
                .orElse(null);

        return (focusTime == null) ? 0 : focusTime.getTotalFocusMinutes();
    }
}
