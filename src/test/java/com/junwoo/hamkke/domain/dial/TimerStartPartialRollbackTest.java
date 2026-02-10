package com.junwoo.hamkke.domain.room.service.transaction;

import com.junwoo.hamkke.container.IntegrationTest;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.listener.TestRoomStatusEventListener;
import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import com.junwoo.hamkke.support.StudyRoomFixture;
import com.junwoo.hamkke.support.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [타이머] 타이머 시작 이벤트 처리 시 트랜잭션 동작을 검증하는 코드입니다.
 * 타이머가 시작될 때 3개의 이벤트 핸들러가 동작하게 되고, 하나의 핸들러에서 예외를 발생시킵니다.
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
@Import(TestRoomStatusEventListener.class)
class TimerStartPartialRollbackTest extends IntegrationTest {

    @Autowired
    TimerStateService timerStateService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRoomRepository studyRoomRepository;

    @Autowired
    ApplicationContext context;

    UserEntity user;
    StudyRoomEntity studyRoom;

    @BeforeEach
    void setUp() {

        // 테스트용 리스너에서 항상 예외를 던지게 설정
        TestRoomStatusEventListener listener = context.getBean(TestRoomStatusEventListener.class);
        listener.setShouldThrow(true);

        user = userRepository.save(UserFixture.create("사용자"));

        studyRoom = studyRoomRepository.save(StudyRoomFixture.create("테스트", user.getId()));

    }

    // @EventListener + @Transactional 조합 시
//    @Test
//    void 타이머_시작이후_하나의_리스너_예외_발생시_다른_리스너는_실행되지_않는다() {
//        // given
//        TimerStartRequest request = new TimerStartRequest(10, 10, 10);
//
//        // when
//        assertThatThrownBy(() -> timerStateService.start(studyRoom.getId(), request))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("테스트용 예외");
//
//        // then -> 집중 시간이 변경되지 않는다.
//        StudyRoomEntity savedRoom = studyRoomRepository.findById(studyRoom.getId()).orElseThrow();
//        assertThat(savedRoom.getFocusMinutes()).isEqualTo(0);
//    }

    // @TransactionalEventListener + Propagation.REQUIRES_NEW 조합 시
//    @Test
//    void 타이머_시작이후_하나의_리스너_예외_발생시_다른_리스너는_정상적으로_실행된다() {
//        // given
//        TimerStartRequest request = new TimerStartRequest(10, 10, 10);
//
//        // when
//        timerStateService.start(studyRoom.getId(), request);
//
//        // then -> 집중 시간이 변경되지 않는다.
//        StudyRoomEntity savedRoom = studyRoomRepository.findById(studyRoom.getId()).orElseThrow();
//        assertThat(savedRoom.getFocusMinutes()).isEqualTo(10);
//    }
}
