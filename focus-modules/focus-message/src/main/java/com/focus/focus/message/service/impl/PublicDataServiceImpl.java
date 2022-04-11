package com.focus.focus.message.service.impl;

import com.focus.focus.message.dao.MessagePublicDataRepository;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import com.focus.focus.message.service.IPublicDataService;
import com.focus.focus.message.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublicDataServiceImpl implements IPublicDataService {
    @Autowired
    private MessagePublicDataRepository publicDataRepository;
    @Autowired
    private IRedisService redisService;

    @Override
    @Transactional
    public MessagePublicDataEntity save(MessagePublicDataEntity publicDataEntity) {
        Optional<MessagePublicDataEntity> opId = publicDataRepository.findById(publicDataEntity.getMessageId());
        if(opId.isPresent()) {
            MessagePublicDataEntity messagePublicDataEntity = opId.get();
            // 更新点赞的数值
            messagePublicDataEntity.setLikeCount(publicDataEntity.getLikeCount());
            MessagePublicDataEntity resPublicDataEntity = publicDataRepository.save(messagePublicDataEntity);
            return resPublicDataEntity;
        }
        return null;
    }

    @Override
    @Transactional
    public List<MessagePublicDataEntity> saveAll(List<MessagePublicDataEntity> publicDataEntities) {
        return publicDataEntities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public void transLikeCountFromRedisToDB() {
        List<MessagePublicDataEntity> publicDataEntitiesR = redisService.getAllLikeCountInRedis();
        saveAll(publicDataEntitiesR);
    }
}
