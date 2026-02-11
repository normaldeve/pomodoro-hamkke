package com.junwoo.hamkke.domain.room.service;

import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.entity.TimerType;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 서버 시작 시 상시 운영 방 초기화
 * - 서버 최초 시작: 4개 상시 운영 방 생성 및 타이머 시작
 * - 서버 재시작: 기존 상시 운영 방의 타이머 재시작
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 7.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermanentRoomInitializer implements ApplicationRunner {

    private final StudyRoomRepository studyRoomRepository;
    private final TimerStateService timerStateService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("[PermanentRoomInitializer] 상시 운영 방 초기화 시작");

        // 이미 존재하는지 확인
        if (studyRoomRepository.existsByPermanentTrue()) {
            log.info("[PermanentRoomInitializer] 기존 상시 운영 방 발견 - 타이머 재시작");
            restartExistingRooms();
        } else {
            log.info("[PermanentRoomInitializer] 최초 실행 - 상시 운영 방 생성");
            createPermanentRooms();
        }
        log.info("[PermanentRoomInitializer] 상시 운영 방 초기화 완료");
    }

    /**
     * 4개 상시 운영 방 생성 및 타이머 시작
     */
    private void createPermanentRooms() {
        createAndStartPermanentRoom(
                "짧게짧게 공부하는 게 좋다면 여기예요!",
                25, 5, TimerType.POMODORO,
                Set.of("시작이반이다")
        );

        createAndStartPermanentRoom(
                "시작하면, 1시간은 공부해야죠",
                55, 5, TimerType.POMODORO,
                Set.of("중간은없다")
        );

        createAndStartPermanentRoom(
                "이 방에 들어오려면, 마음의 준비는 필수예요",
                80, 10, TimerType.FLIP,
                Set.of("각오완료")
        );

        createAndStartPermanentRoom(
                "한 번 시작하면, 끝을 보는 스타일입니다",
                110, 10, TimerType.FLIP,
                Set.of("끝까지가자")
        );
    }

    /**
     * 개별 상시 운영 방 생성 및 타이머 시작
     */
    private void createAndStartPermanentRoom(
            String title,
            int focusMinutes,
            int breakMinutes,
            TimerType timerType,
            Set<String> hashtags
    ) {
        log.info("[PermanentRoomInitializer] 상시 운영 방 생성 시작 - title: {}, focus: {}분, break: {}분",
                title, focusMinutes, breakMinutes);

        StudyRoomEntity room = StudyRoomEntity.createPermanentRoom(
                title,
                focusMinutes,
                breakMinutes,
                timerType,
                hashtags
        );

        StudyRoomEntity savedRoom = studyRoomRepository.save(room);

        log.info("[PermanentRoomInitializer] 상시 운영 방 생성 완료 - roomId: {}, title: {}", savedRoom.getId(), title);

        timerStateService.startPermanent(savedRoom.getId(), focusMinutes, breakMinutes);

        log.info("[PermanentRoomInitializer] 타이머 시작 완료 - roomId: {}", savedRoom.getId());
    }

    /**
     * 기존 상시 운영 방의 타이머 재시작
     */
    private void restartExistingRooms() {
        List<StudyRoomEntity> permanentRooms = studyRoomRepository.findByPermanentTrue();

        log.info("[PermanentRoomInitializer] 재시작할 상시 운영 방 개수: {}", permanentRooms.size());

        for (StudyRoomEntity room : permanentRooms) {
            log.info("[PermanentRoomInitializer] 타이머 재시작 - roomId: {}, title: {}, focus: {}분, break: {}분",
                    room.getId(), room.getTitle(), room.getFocusMinutes(), room.getBreakMinutes());

            timerStateService.startPermanent(room.getId(), room.getFocusMinutes(), room.getBreakMinutes());

            log.info("[PermanentRoomInitializer] 타이머 재시작 완료 - roomId: {}", room.getId());
        }
    }
}