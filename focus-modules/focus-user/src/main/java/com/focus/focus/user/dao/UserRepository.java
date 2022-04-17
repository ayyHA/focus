package com.focus.focus.user.dao;

import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    // 根据username获取用户实体
    UserEntity findByUsername(String username);
    // 根据username查是否存在
    Boolean existsByUsername(String username);
    // 模糊匹配nickname
    List<UserEntity> findByNicknameContaining(String nickname);
}
