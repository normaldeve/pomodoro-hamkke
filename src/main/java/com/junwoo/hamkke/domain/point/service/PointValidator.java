package com.junwoo.hamkke.domain.point.service;

import com.junwoo.hamkke.domain.point.entity.PointType;
import com.junwoo.hamkke.domain.point.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PointValidator {

    private final PointLogRepository pointLogRepository;

    public boolean validatePointIssuable(PointType type, Long refId) {
        if (pointLogRepository.existsByTypeAndRefId(type, refId)) {
            log.warn("[PointService] 중복 포인트 지급 시도 방지 - type: {}, refId: {}", type, refId);
            return false;
        }

        return true;
    }
}
