package com.focus.focus.message.service;

import com.focus.focus.api.dto.MessageDto;
import com.focus.focus.api.dto.MessageInfoDto;

import java.util.List;

public interface IMessageService {
    Boolean save(MessageDto messageDto);
    List<MessageInfoDto> showMessage(Integer page);
}
