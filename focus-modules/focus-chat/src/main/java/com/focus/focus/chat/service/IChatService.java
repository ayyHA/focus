package com.focus.focus.chat.service;

import com.focus.focus.api.dto.ChatDto;
import com.focus.focus.chat.domain.entity.ChatEntity;

public interface IChatService {
    void updateChatEntity(ChatDto chatDto);
    ChatDto updateChatEntity(ChatEntity chatEntity);
}
