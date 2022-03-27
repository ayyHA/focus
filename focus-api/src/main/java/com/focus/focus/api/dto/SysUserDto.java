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
public class SysUserDto implements Serializable {
    private final static long serialVersionUID = 1L;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Date updateAt;
}
