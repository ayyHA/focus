package com.focus.focus.auth.dao;

import com.focus.focus.auth.model.po.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission,Long> {
    public List<SysRolePermission> findByPermissionId(Long permissionId);
}
