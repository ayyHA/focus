package com.focus.focus.api.dto;

import com.focus.focus.api.enumerate.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String nickname;
    private String email;
    private Date createAt;
    private String description;
    private Date birthday;
    private GenderEnum gender;
    private Long pinnedMessageId;
    private String profileImageUrl;
    private String avatarUrl;
    private Long dunDunCoin;
}
