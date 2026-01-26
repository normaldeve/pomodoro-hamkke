package com.junwoo.hamkke.domain.room.service;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.room_member.service.StudyRoomMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@ActiveProfiles("test")
@SpringBootTest
class StudyRoomMemberServiceTest {

    @Autowired
    private StudyRoomMemberService studyRoomMemberService;

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private StudyRoomMemberRepository studyRoomMemberRepository;

    @AfterEach
    void tearDown() {
        studyRoomMemberRepository.deleteAll();
        studyRoomRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 20명이 입장 요청하면 정원 10명만 입장된다")
    void enterRoom_concurrency_test() throws InterruptedException {

        // ======================
        // given
        // ======================
        int maxParticipants = 10;
        int totalRequests = 20;

        StudyRoomEntity room = StudyRoomEntity.builder()
                .title("동시성 테스트 방")
                .description("정원 초과 테스트")
                .maxParticipants(maxParticipants)
                .currentParticipants(0)
                .secret(false)
                .status(RoomStatus.WAITING)
                .build();

        StudyRoomEntity savedRoom = studyRoomRepository.saveAndFlush(room);

        ExecutorService executorService = Executors.newFixedThreadPool(totalRequests);

        CountDownLatch latch = new CountDownLatch(totalRequests);

        // ======================
        // when
        // ======================
        for (int i = 0; i < totalRequests; i++) {
            final Long userId = (long) (i + 1);

            executorService.submit(() -> {
                try {
                    studyRoomMemberService.enterRoom(
                            savedRoom.getId(),
                            userId,
                            null
                    );
                } catch (Exception e) {
                    // 정원 초과 / 중복 입장 예외는 정상 시나리오
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // ======================
        // then
        // ======================
        long memberCount =
                studyRoomMemberRepository.countByStudyRoomId(savedRoom.getId());

        StudyRoomEntity updatedRoom =
                studyRoomRepository.findById(savedRoom.getId())
                        .orElseThrow();

        assertThat(memberCount).isEqualTo(maxParticipants);
        assertThat(updatedRoom.getCurrentParticipants()).isEqualTo(maxParticipants);
    }
}