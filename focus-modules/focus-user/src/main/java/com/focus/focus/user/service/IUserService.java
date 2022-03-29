package com.focus.focus.user.service;

import com.focus.focus.user.domain.entity.UserEntity;

public interface IUserService {
    Boolean updateUserAvatar(String path);
    UserEntity getUserInfoByUsername(String username);
}
