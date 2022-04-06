package com.focus.focus.user.service;

import com.focus.focus.api.dto.UserInfoDto;

public interface IUserService {
    Boolean updateUserAvatar(String path);
    Boolean updateUserBackground(String path);
    UserInfoDto getUserInfoByUsername(String username);
    UserInfoDto updateUserDetails(UserInfoDto dto);
}
