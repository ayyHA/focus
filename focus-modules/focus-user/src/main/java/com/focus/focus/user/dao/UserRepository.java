package com.focus.focus.user.dao;

import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    public UserEntity findByUsername(String username);
}
