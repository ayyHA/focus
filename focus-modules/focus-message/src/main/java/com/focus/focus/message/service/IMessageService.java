package com.focus.focus.message.service;

import com.focus.focus.api.dto.MessageDto;
import com.focus.focus.api.dto.MessageInfoDto;
import com.focus.focus.api.util.LoginVal;
import com.focus.focus.message.domain.entity.MessageEntity;

import java.util.List;

//import com.focus.auth.common.model.LoginVal;

public interface IMessageService {
//    Boolean save(MessageDto messageDto);
//    Long save(MessageDto messageDto);
    MessageInfoDto save(MessageDto messageDto);
    List<MessageInfoDto> showMessage(Integer page);
    List<MessageDto> searchByKeywords(String keywords);
    List<MessageInfoDto> getMsgInfoDtos(List<MessageDto> messageDtos, LoginVal loginVal);
    List<MessageInfoDto> getMsgInfoDtosByAuthorId(String authorId,Integer pageNum);
    MessageInfoDto getPinnedMsgInfoDto(String userId);
    Boolean deleteMessageById(Long messageId);
    List<MessageInfoDto> getReplies(String inReplyToAuthorId,Long conversationId);
    MessageInfoDto getMessageInfoDto(Long messageId);
    MessageInfoDto getMessageInfoDto(MessageEntity entity);
    MessageInfoDto getRetweetedInfoDto(Long conversationId);
}
