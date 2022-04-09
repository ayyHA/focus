package com.focus.focus.message.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.MessagePublicDataDto;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MessagePublicDataConvertor extends BaseConvertor<MessagePublicDataEntity, MessagePublicDataDto> {
    @Override
    public Function<MessagePublicDataEntity, MessagePublicDataDto> functionConvertToDTO() {
        return messagePublicDataEntity -> {
            return MessagePublicDataDto.builder()
                    .likeCount(messagePublicDataEntity.getLikeCount())
                    .replyCount(messagePublicDataEntity.getReplyCount())
                    .retweetCount(messagePublicDataEntity.getRetweetCount())
                    .build();
        };
    }

    @Override
    public Function<MessagePublicDataDto, MessagePublicDataEntity> functionConvertToEntity() {
        return messagePublicDataDto -> {
            return MessagePublicDataEntity.builder()
                    .likeCount(messagePublicDataDto.getLikeCount())
                    .replyCount(messagePublicDataDto.getReplyCount())
                    .retweetCount(messagePublicDataDto.getRetweetCount())
                    .build();
        };
    }
}
