package com.focus.focus.user.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.api.enumerate.LangSelect;
import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AuthUserConvertor extends BaseConvertor<UserEntity, SysUserDto>{
    @Override
    public Function<UserEntity, SysUserDto> functionConvertToDTO() {
        return userEntity -> {
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
            return UserEntity.builder()
                    .username(sysUserDto.getUsername())
                    .password(sysUserDto.getPassword())
                    .nickname(sysUserDto.getNickname())
                    .email(sysUserDto.getEmail())
                    .lang(LangSelect.chinese)
                    .dunDunCoin(0L)
                    .avatarUrl("http://r9gseewjp.hn-bkt.clouddn.com/user.png")
                    .profileImageUrl("http://r9gseewjp.hn-bkt.clouddn.com/profileImageUrl.png")
                    .description("啥也没有...")
                    .build();
        };
    }
}
