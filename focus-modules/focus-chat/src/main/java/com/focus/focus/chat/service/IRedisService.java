package com.focus.focus.chat.service;

import com.focus.focus.api.dto.ChatDto;

import java.util.Date;
import java.util.List;

public interface IRedisService {
    // 将MySQL中的消息同步到Redis中，此用于聊天集合，zset存储
    Boolean transChatZSetToRedis(String userId);
    // 获取Redis中的聊天集合信息
    List<ChatDto> getChatZSetFromRedis(String userId);
    // 将MySQL中的消息同步到Redis中，此用于历史聊天记录，list存储
    Boolean transChatHistoryToRedis(String userId,String talkId);
    // 获取Redis中的历史聊天记录
    List<ChatDto> getChatHistoryFromRedis(String userId);
    // 更新Redis中的聊天集合信息
    void updateChatZSetInRedis(String userId, String chatUserId, Date date);
    // 更新呢Redis中的历史聊天记录
    void updateChatHistoryInRedis(String userId,ChatDto chatDto);
    // 是否存在该key，该key数据是否非零[ZSet]
    Boolean existZSetKeyAndNotEqZero(String key);
    // 是否存在该key，该key数据是否非零[list]
    Boolean existListKeyAndNotEqZero(String key);
}
