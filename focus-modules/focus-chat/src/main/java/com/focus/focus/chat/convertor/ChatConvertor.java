package com.focus.focus.chat.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.ChatDto;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.chat.domain.entity.ChatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class ChatConvertor extends BaseConvertor<ChatEntity, ChatDto> {
    private final UserClient userClient;

    @Override
    public Function<ChatEntity, ChatDto> functionConvertToDTO() {
        return chatEntity -> {
            return  ChatDto.builder()
                    .createAt(chatEntity.getCreateAt())
                    .status(chatEntity.getStatus())
                    .text(chatEntity.getText())
                    .sourceUser(userClient.getUserInfoDtoById(chatEntity.getChatId().getSourceId()))
                    .targetUser(userClient.getUserInfoDtoById(chatEntity.getChatId().getTargetId()))
                    .build();
        };
    }

    @Override
    public Function<ChatDto, ChatEntity> functionConvertToEntity() {
        return chatDto -> {
            return ChatEntity.builder()
                    .chatId(new ChatEntity.ChatId(chatDto.getSourceUser().getId(),chatDto.getTargetUser().getId()))
                    .createAt(chatDto.getCreateAt())
                    .status(chatDto.getStatus())
                    .text(chatDto.getText())
                    .build();
        };
    }
}
