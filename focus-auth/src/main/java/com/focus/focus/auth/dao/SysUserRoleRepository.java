package com.focus.focus.auth.dao;

import com.focus.focus.auth.model.po.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole,Long> {
    public List<SysUserRole> findByUserId(Long userId);
}
