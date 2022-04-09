package com.focus.focus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 此类用于前端讯息展示。其余几个类不写作内部类原因在于后续编码又要抽出来
public class MessageInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageDto messageDto;
    private UserInfoDto userInfoDto;
    private MessagePublicDataDto messagePublicDataDto;
    private MessageStatusDto messageStatusDto;
    // 页数(offset)的上限
    private Integer maxPages;
    // message的总数
    private Long maxElements;
}
