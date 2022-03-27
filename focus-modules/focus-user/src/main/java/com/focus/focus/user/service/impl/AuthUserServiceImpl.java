package com.focus.focus.user.service.impl;

import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.user.convertor.AuthUserConvertor;
import com.focus.focus.user.dao.UserRepository;
import com.focus.focus.user.domain.entity.UserEntity;
import com.focus.focus.user.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements IAuthUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthUserConvertor authUserConvertor;

    @Override
    public SysUserDto save(SysUserDto sysUserDto) {
        String username = sysUserDto.getUsername();
        // 检测username是否唯一，后期需要给自定义异常抛出
        if(userRepository.existsByUsername(username))
            return null;
        UserEntity userEntity = authUserConvertor.convertToEntity(sysUserDto);
        userRepository.save(userEntity);
        return authUserConvertor.convertToDTO(userEntity);
    }
}
