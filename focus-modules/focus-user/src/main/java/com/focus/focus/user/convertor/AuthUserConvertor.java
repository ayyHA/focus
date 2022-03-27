package com.focus.focus.user.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AuthUserConvertor extends BaseConvertor<UserEntity, SysUserDto>{
    @Override
    public Function<UserEntity, SysUserDto> functionConvertToDTO() {
        return userEntity -> {
//            SysUserDto sysUserDto = new SysUserDto();
            return SysUserDto.builder()
                    .username(userEntity.getUsername())
                    .nickname(userEntity.getNickname())
                    .email(userEntity.getEmail())
                    .build();
        };
    }

    @Override
    public Function<SysUserDto, UserEntity> functionConvertToEntity() {
        return sysUserDto -> {
//            UserEntity userEntity = new UserEntity();
            return UserEntity.builder()
                    .username(sysUserDto.getUsername())
                    .password(sysUserDto.getPassword())
                    .nickname(sysUserDto.getNickname())
                    .email(sysUserDto.getEmail())
                    .build();
        };
    }
}
