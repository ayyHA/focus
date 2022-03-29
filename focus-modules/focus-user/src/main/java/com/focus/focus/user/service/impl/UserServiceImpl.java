package com.focus.focus.user.service.impl;

import com.focus.auth.common.model.LoginVal;
import com.focus.auth.common.utils.OauthUtils;
import com.focus.focus.user.dao.UserRepository;
import com.focus.focus.user.domain.entity.UserEntity;
import com.focus.focus.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Boolean updateUserAvatar(String path) {
        LoginVal loginVal = OauthUtils.getCurrentUser();
        log.info("USERNAME: {}",loginVal.getUsername());
        UserEntity userEntity = userRepository.findByUsername(loginVal.getUsername());
        userEntity.setAvatarUrl(path);
        /* save动态更新 */
        UserEntity afterSaveEntity = userRepository.save(userEntity);
        if(Objects.isNull(afterSaveEntity))
            return false;
        return true;
    }

    @Override
    public UserEntity getUserInfoByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if(Objects.isNull(userEntity))
            return null;
        return userEntity;
    }

}
