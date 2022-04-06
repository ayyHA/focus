package com.focus.focus.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.focus.focus.api.dto.MessageDto;
import com.focus.focus.api.enumerate.MessageTypeEnum;
import com.focus.focus.message.convertor.MessageConvertor;
import com.focus.focus.message.dao.MessageRepository;
import com.focus.focus.message.domain.entity.MessageEntity;
import com.focus.focus.message.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private MessageConvertor messageConvertor;
    @Autowired
    private MessageRepository messageRepository;


    @Override
    public Boolean save(MessageDto messageDto) {
       // 类型转换
       MessageEntity message = messageConvertor.convertToEntity(messageDto);
       // message的类型为回复，但没有指向回复的讯息的作者ID或是讯息ID
       if(message.getType() == MessageTypeEnum.replied_to &&
               (message.getConversationId()==null ||
                message.getInReplyToAuthorId()==null||
                message.getInReplyToAuthorId().equals(""))){
           return false;
       }
       // 持久化
       MessageEntity save = messageRepository.save(message);
       if(ObjectUtil.isEmpty(save))
           return false;
       return true;
    }
}
