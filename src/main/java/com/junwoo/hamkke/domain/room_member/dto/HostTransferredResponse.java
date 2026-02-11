package com.junwoo.hamkke.domain.room_member.dto;

import lombok.Builder;

/**
 * 방장 권한 위임 웹소켓 응답
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Builder
public record HostTransferredResponse(
        Long previousHostId,
        String previousHostNickname,
        Long newHostId,
        String newHostNickname,
        boolean isAutoTransfer
) {

    public static HostTransferredResponse of(
            Long previousHostId,
            String previousHostNickname,
            Long newHostId,
            String newHostNickname,
            boolean isAutoTransfer
    ) {
        return HostTransferredResponse.builder()
                .previousHostId(previousHostId)
                .previousHostNickname(previousHostNickname)
                .newHostId(newHostId)
                .newHostNickname(newHostNickname)
                .isAutoTransfer(isAutoTransfer)
                .build();
    }
}
