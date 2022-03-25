package com.focus.focus.auth.dao;

import com.focus.focus.auth.model.po.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole,Long> {
}
