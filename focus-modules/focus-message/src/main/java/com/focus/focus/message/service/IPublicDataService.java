package com.focus.focus.message.service;

import com.focus.focus.message.domain.entity.MessagePublicDataEntity;

import java.util.List;

public interface IPublicDataService {
    MessagePublicDataEntity save(MessagePublicDataEntity publicDataEntity);
    List<MessagePublicDataEntity> saveAll(List<MessagePublicDataEntity> publicDataEntities);
    void transLikeCountFromRedisToDB();
}
