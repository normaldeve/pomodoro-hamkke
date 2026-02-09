package com.junwoo.hamkke.domain.room.service.transaction;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * [타이머] 타이머 시작 이벤트 처리 시 트랜잭션 동작을 검증하는 코드입니다.
 * 타이머가 시작될 때 3개의 이벤트 핸들러가 동작하게 되고, 하나의 핸들러에서 예외를 발생시킵니다.
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
class TimerStartPartialRollbackTest {

}
