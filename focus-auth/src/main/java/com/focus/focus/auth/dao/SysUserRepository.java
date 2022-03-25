package com.focus.focus.auth.dao;

import com.focus.focus.api.enumerate.SysUserStatus;
import com.focus.focus.auth.model.po.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser,Long> {
    public SysUser findByUsername(String username);
    public SysUser findByUsernameAndStatus(String username, SysUserStatus status);
}
