package com.focus.focus.message.service;

import com.focus.focus.api.dto.MessageDto;

public interface IMessageService {
    Boolean save(MessageDto messageDto);
}
