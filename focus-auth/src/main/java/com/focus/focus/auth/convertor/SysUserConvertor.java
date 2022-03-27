package com.focus.focus.auth.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.api.enumerate.SysUserStatus;
import com.focus.focus.auth.model.po.SysUser;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SysUserConvertor extends BaseConvertor<SysUser, SysUserDto> {
    /**
     * SysUser -> SysUserDto
     */
    @Override
    public Function<SysUser, SysUserDto> functionConvertToDTO() {
        return sysUser -> {
//            SysUserDto sysUserDto = new SysUserDto();
            return SysUserDto.builder()
                    .username(sysUser.getUsername())
                    .password(sysUser.getPassword())
                    .updateAt(sysUser.getUpdateAt())
                    .build();
        };
    }

    @Override
    public Function<SysUserDto, SysUser> functionConvertToEntity() {
        return sysUserDto -> {
//            SysUser sysUser = new SysUser();
            return SysUser.builder()
                    .username(sysUserDto.getUsername())
                    .password(sysUserDto.getPassword())
                    .updateAt(sysUserDto.getUpdateAt())
                    .status(SysUserStatus.normal)
                    .build();
        };
    }
}
