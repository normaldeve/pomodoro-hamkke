package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.container.IntegrationTest;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import com.junwoo.hamkke.support.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 11.
 */
class StudyRoomMemberServiceTest extends IntegrationTest {
    @Autowired
    private StudyRoomMemberService studyRoomMemberService;

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRoomMemberRepository studyRoomMemberRepository;

    @Test
    void 동시에_입장하면_정확히_정원만큼만_성공한다() throws Exception {
        // given
        int maxParticipants = 5;
        int threadCount = 30;           // 20보다 조금 더 크게

        StudyRoomEntity room = studyRoomRepository.save(
                StudyRoomEntity.builder()
                        .title("테스트 방")
                        .maxParticipants(maxParticipants)
                        .currentParticipants(0)
                        .secret(false)
                        .build()
        );
        UUID roomId = room.getId();

        // 유저 미리 영속화
        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            UserEntity u = UserFixture.create("참여자" + i);
            users.add(userRepository.save(u));
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger(0);

        for (UserEntity user : users) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    studyRoomMemberService.enterRoom(roomId, user.getId(), null);
                    success.incrementAndGet();
                } catch (StudyRoomException e) {
                    // 정원 초과 외의 예외는 여기서 터지면 테스트 실패로 간주해도 됨
                    if (e.getErrorCode() != ErrorCode.ROOM_CAPACITY_EXCEEDED) {
                        throw new RuntimeException("예상치 못한 실패: " + e.getErrorCode(), e);
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 모든 스레드가 준비될 때까지 대기 (필수는 아니지만 더 정확)
        Thread.sleep(200);
        startLatch.countDown();

        endLatch.await();
        executor.shutdown();

        // then
        StudyRoomEntity updated = studyRoomRepository.findById(roomId).orElseThrow();

        assertThat(success.get())
                .as("성공한 입장 수")
                .isEqualTo(maxParticipants);

        assertThat(updated.getCurrentParticipants())
                .as("DB에 기록된 참여자 수")
                .isEqualTo(maxParticipants);

        long actualMemberCount = studyRoomMemberRepository.countByStudyRoomId(roomId);
        assertThat(actualMemberCount).isEqualTo(maxParticipants);
    }
}