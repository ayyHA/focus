package com.focus.focus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageDto messageDto;
    private UserInfoDto userInfoDto;
    private Date createAt;
}
