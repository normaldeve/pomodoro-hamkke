package com.junwoo.hamkke.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 1.
 */
@Getter
@RequiredArgsConstructor
public enum PointPolicy {

    STUDY_FOCUS(1, "집중 시간 1분"),
    REFLECTION_WITHOUT_IMAGE(5, "사진 첨부 없이 회고 작성"),
    REFLECTION_WITH_IMAGE(10, "이미지와 함께 회고 작성");

    private final int point;
    private final String description;
}
