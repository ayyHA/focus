package com.focus.focus.auth.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.focus.auth.common.model.SecurityUser;
import com.focus.auth.common.model.SysConstant;
import com.focus.focus.api.enumerate.SysUserStatus;
import com.focus.focus.auth.dao.SysRoleRepository;
import com.focus.focus.auth.dao.SysUserRepository;
import com.focus.focus.auth.dao.SysUserRoleRepository;
import com.focus.focus.auth.model.po.SysUser;
import com.focus.focus.auth.model.po.SysUserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Primary
@Service("jwtUserDetailsService")
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;

    /**
     * 根据username从数据库找到对应名字的用户，用SecurityUser封装之
     * @param username
     * @return SecurityUser extends UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserRepository.findByUsernameAndStatus(username, SysUserStatus.normal);
        if(Objects.isNull(sysUser)){
            throw new UsernameNotFoundException("用户不存在");
        }
        List<SysUserRole> sysUserRoles = sysUserRoleRepository.findByUserId(sysUser.getId());
        List<String> roles = new ArrayList<>();
        sysUserRoles.forEach((sysUserRole)->{
            sysRoleRepository.findById(sysUserRole.getRoleId()).ifPresent(role->roles.add(SysConstant.ROLE_PREFIX + role.getRoleCode()));
        });
        log.info("User:{},Role{}",username,roles.toString());
        return SecurityUser.builder()
                .username(username)
                .password(sysUser.getPassword())
                .authorities(AuthorityUtils.createAuthorityList(ArrayUtil.toArray(roles,String.class)))
                .build();
    }
}
