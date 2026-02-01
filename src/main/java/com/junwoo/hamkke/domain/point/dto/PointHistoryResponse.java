package com.junwoo.hamkke.domain.point.dto;

import com.junwoo.hamkke.domain.point.entity.PointLogEntity;
import com.junwoo.hamkke.domain.point.entity.PointType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
@Builder
public record PointHistoryResponse(
        Long id,
        PointType type,
        int amount,
        LocalDateTime createdAt
) {

    public static PointHistoryResponse from(PointLogEntity entity) {
        return PointHistoryResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
