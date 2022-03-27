package com.focus.focus.auth.service.impl;

import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.api.feign.AuthUserClient;
import com.focus.focus.auth.convertor.SysUserConvertor;
import com.focus.focus.auth.dao.SysUserRepository;
import com.focus.focus.auth.dao.SysUserRoleRepository;
import com.focus.focus.auth.model.po.SysUser;
import com.focus.focus.auth.model.po.SysUserRole;
import com.focus.focus.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthUserClient authUserClient;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    @Autowired
    private SysUserConvertor sysUserConvertor;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUserDto saveSysUser(SysUserDto sysUserDto) {
        String username = sysUserDto.getUsername();
        // 用户名唯一，后期有空补上自定义异常
        if(sysUserRepository.existsByUsername(username)){
            return null;
        }
        // 转换为SysUser并加密存入数据库
        SysUser sysUser = sysUserConvertor.convertToEntity(sysUserDto);
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        SysUser afterSaveSysUser = sysUserRepository.save(sysUser);
        // 远程调用/user/signUp
        log.info("Before Save UserEntity");
        authUserClient.signUp(sysUserDto);
        SysUserRole sysUserRole = SysUserRole.builder()
                .userId(afterSaveSysUser.getId())
                .roleId(2L)
                .build();
        log.info("SysUserRole INFO:{}",sysUserRole.toString());
        sysUserRoleRepository.save(sysUserRole);
        return sysUserConvertor.convertToDTO(sysUser);
    }

}
