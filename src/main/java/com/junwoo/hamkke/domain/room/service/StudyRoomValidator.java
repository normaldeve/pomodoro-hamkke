package com.junwoo.hamkke.domain.room.service;

import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Component
@RequiredArgsConstructor
public class StudyRoomValidator {

    private final StudyRoomRepository studyRoomRepository;

}
