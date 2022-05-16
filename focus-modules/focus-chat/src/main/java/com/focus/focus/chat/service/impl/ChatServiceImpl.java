package com.focus.focus.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.focus.focus.api.dto.ChatDto;
import com.focus.focus.chat.convertor.ChatConvertor;
import com.focus.focus.chat.dao.ChatRepository;
import com.focus.focus.chat.domain.entity.ChatEntity;
import com.focus.focus.chat.service.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements IChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatConvertor chatConvertor;

    // 对聊天记录进行持久化存储
    @Override
    public void updateChatEntity(ChatDto chatDto) {
        if(ObjectUtil.isEmpty(chatDto))
            return;
        ChatEntity chatEntity = chatConvertor.convertToEntity(chatDto);
        chatRepository.save(chatEntity);
    }

    @Override
    public ChatDto updateChatEntity(ChatEntity chatEntity) {
        if(ObjectUtil.isEmpty((chatEntity)))
            return null;
        ChatEntity afterSaveEntity = chatRepository.save(chatEntity);
        ChatDto chatDto = chatConvertor.convertToDTO(afterSaveEntity);
        return chatDto;
    }
}