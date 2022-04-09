package com.focus.focus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageStatusDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 当前用户是否喜欢此讯息
    private Boolean likeStatus;
    // 当前用户是否转发过此讯息
    private Boolean retweetStatus;
}
