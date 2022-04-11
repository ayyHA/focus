package com.focus.focus.message.service.impl;

import com.focus.focus.api.enumerate.MessageOperateStatus;
import com.focus.focus.message.dao.LikeRepository;
import com.focus.focus.message.domain.entity.LikeEntity;
import com.focus.focus.message.service.ILikeService;
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
public class LikeServiceImpl implements ILikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private IRedisService redisService;

    // 存的时候报错，rollback
    @Override
    @Transactional
    public LikeEntity save(LikeEntity likeEntity) {
        LikeEntity.LikeId id = likeEntity.getId();
        Optional<LikeEntity> opId = likeRepository.findById(id);
        // 若已有此数据，则遵循原来是否已发送的status
        if(opId.isPresent()){
            LikeEntity entity = opId.get();
            MessageOperateStatus status = entity.getStatus();
            likeEntity.setStatus(status);
        }
        LikeEntity resLikeEntity = likeRepository.save(likeEntity);
        log.info("Like save => [{}]",resLikeEntity.toString());
        return resLikeEntity;
    }

    @Override
    @Transactional
    public List<LikeEntity> saveAll(List<LikeEntity> likeEntities) {
        // 因为存在数据是否已发送的状态问题，故不适用JPA自带的saveAll,使用自定义的save。
        log.info("Like's saveAll");
        return likeEntities.stream().map(this::save).collect(Collectors.toList());
    }

    // 通过Quartz定时执行
    @Override
    public void transLikeFromRedisToDB() {
        log.info("Quartz entry transLikeFromRedisToDB");
        List<LikeEntity> likeEntitiesR = redisService.getAllLikeInRedis();
        saveAll(likeEntitiesR);
        log.info("Quartz leave transLikeFromRedisToDB");
    }
}
