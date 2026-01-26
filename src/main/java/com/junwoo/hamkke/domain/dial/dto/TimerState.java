package com.junwoo.hamkke.domain.dial.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Data
@Builder
public class TimerState {
    private Long roomId;
    private Integer minutes;
    private Integer remainingSeconds;
    private Boolean isRunning;
    private Long startTime;
}
