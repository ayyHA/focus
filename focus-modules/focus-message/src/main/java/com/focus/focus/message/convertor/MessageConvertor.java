package com.focus.focus.message.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.MessageDto;
import com.focus.focus.message.domain.entity.MessageEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MessageConvertor extends BaseConvertor<MessageEntity,MessageDto> {
    @Override
    public Function<MessageEntity, MessageDto> functionConvertToDTO() {
        return (messageEntity) -> {
            return MessageDto.builder()
                    .text(messageEntity.getText())
                    .images(messageEntity.getImages())
                    .authorId(messageEntity.getAuthorId())
                    .createAt(messageEntity.getCreateAt())
                    .type(messageEntity.getType())
                    .conversationId(messageEntity.getConversationId())
                    .inReplyToAuthorId(messageEntity.getInReplyToAuthorId())
                    .keywords(messageEntity.getKeywords())
                    .grantType(messageEntity.getGrantType())
                    .build();
        };
    }

    @Override
    public Function<MessageDto, MessageEntity> functionConvertToEntity() {
        return messageDto -> {
            return MessageEntity.builder()
                    .text(messageDto.getText())
                    .images(messageDto.getImages())
                    .authorId(messageDto.getAuthorId())
                    .type(messageDto.getType())
                    .conversationId(messageDto.getConversationId())
                    .inReplyToAuthorId(messageDto.getInReplyToAuthorId())
                    .keywords(messageDto.getKeywords())
                    .grantType(messageDto.getGrantType())
                    .build();
        };
    }
}
