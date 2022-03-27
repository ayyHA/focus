package com.focus.focus.user.dao;

import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    // 根据username获取用户实体
    public UserEntity findByUsername(String username);
    // 根据username查是否存在
    public Boolean existsByUsername(String username);
}
