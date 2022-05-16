package com.focus.focus.api.dto;

import com.focus.focus.api.enumerate.ChatStatus;
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
public class ChatDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private UserInfoDto sourceUser;
    private UserInfoDto targetUser;
    private Date createAt;
    private String text;
    private ChatStatus status;
}
