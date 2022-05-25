package com.focus.focus.chat.dao;

import com.focus.focus.chat.domain.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,ChatEntity.ChatId> {
    // 寻找所有与sourceId相关的聊天记录
    List<ChatEntity> findByChatIdSourceId(String sourceId);
    // 寻找所有与targetId相关的聊天记录
    List<ChatEntity> findByChatIdTargetId(String targetId);
    // 寻找匹配sourceId和targetId的聊天记录，用于获取历史消息
    List<ChatEntity> findByChatIdSourceIdAndChatIdTargetId(String sourceId,String targetId);
}
