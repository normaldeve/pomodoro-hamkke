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
        String previousHostProfileUrl,
        Long newHostId,
        String newHostNickname,
        String newHostProfileUrl,
        boolean isAutoTransfer
) {

    public static HostTransferredResponse of(
            Long previousHostId,
            String previousHostNickname,
            String previousHostProfileUrl,
            Long newHostId,
            String newHostNickname,
            String newHostProfileUrl,
            boolean isAutoTransfer
    ) {
        return HostTransferredResponse.builder()
                .previousHostId(previousHostId)
                .previousHostNickname(previousHostNickname)
                .previousHostProfileUrl(previousHostProfileUrl)
                .newHostId(newHostId)
                .newHostNickname(newHostNickname)
                .newHostProfileUrl(newHostProfileUrl)
                .isAutoTransfer(isAutoTransfer)
                .build();
    }
}
