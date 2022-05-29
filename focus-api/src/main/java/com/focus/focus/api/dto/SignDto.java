package com.focus.focus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignDto implements Serializable {
    private static long serialVersionUID = 1L;

    // 当月签到连续天数
    private Integer signContinuous;
    // 当月签到总次数
    private Integer signCount;
    // 当天签到状态 0-未签到 1-已签到
    private Boolean signStatus;
    // 当天签到获得的盾盾币数量
    private Long signDunDunCoin;
}
