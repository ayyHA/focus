package com.focus.focus.message.dao;

import com.focus.focus.message.domain.entity.MessageOperateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageOperateRepository extends JpaRepository<MessageOperateEntity, MessageOperateEntity.MessageOperateId> {
}
