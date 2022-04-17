package com.focus.focus.message.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.focus.focus.api.dto.LikeDto;
import com.focus.focus.api.dto.MessageDto;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.api.enumerate.LikeStatus;
import com.focus.focus.api.enumerate.MessageOperateStatus;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.api.util.RabbitMqConstant;
import com.focus.focus.message.component.RabbitMqProducer;
import com.focus.focus.message.convertor.MessageConvertor;
import com.focus.focus.message.dao.LikeRepository;
import com.focus.focus.message.dao.MessageRepository;
import com.focus.focus.message.domain.entity.LikeEntity;
import com.focus.focus.message.domain.entity.MessageEntity;
import com.focus.focus.message.service.ILikeService;
import com.focus.focus.message.service.IRedisService;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class LikeServiceImpl implements ILikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageConvertor messageConvertor;
    // 用于远程调用
    private final UserClient userClient;

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

    // 计算新增的点赞数量，发给author
    @Override
    public void sendLikeNotification() {
        // 获取like且unsent的likeEntities
        List<LikeEntity> likeEntities = likeRepository.findByStatusAndLikeStatus(MessageOperateStatus.unsent, LikeStatus.like);
        // String: authorId;Integer: count
        Map<String,Integer> authorIdWithCounts = new HashMap<>();
        likeEntities.forEach(likeEntity -> {
            Long messageId = likeEntity.getId().getMessageId();
            Optional<MessageEntity> opMessageEntity = messageRepository.findById(messageId);
            opMessageEntity.ifPresent(messageEntity -> {
                Integer oldCount = authorIdWithCounts.get(messageEntity.getAuthorId());
                if(null == oldCount)
                    authorIdWithCounts.put(messageEntity.getAuthorId(),1);
                else
                    authorIdWithCounts.put(messageEntity.getAuthorId(),oldCount + 1);
            });
        });
        log.info("send like message to MQ");
        // 将获取到的新增点赞信息发布到以authorId为queueName的消息队列中，前端通过exchangeName和routingKey进行获取即可
        authorIdWithCounts.entrySet().forEach(entry->{
            RabbitMqProducer.sendMessage(RabbitMqConstant.LIKE_EXCHANGE, BuiltinExchangeType.DIRECT,entry.getKey(),
                    entry.getKey(),"你获得了" + entry.getValue() + "条新的点赞信息");
        });
        log.info("after send like message to MQ");
        // 将已发送至MQ的消息置为sent,更新至MySQL中
        likeEntities.forEach(likeEntity -> {likeEntity.setStatus(MessageOperateStatus.sent);});
        likeRepository.saveAll(likeEntities);
    }

    // Map主要返回nickname[UserEntity]，content[MessageEntity]
    @Override
    public List<LikeDto> getLikeData(String userId) {
        // 根据userId查找所发布的讯息集
        List<MessageEntity> messageEntities = messageRepository.findByAuthorId(userId);
        if(CollectionUtil.isEmpty(messageEntities)){
            return null;
        }
        // 获取messageEntities对应的主键列表
        List<Long> messageIds = new ArrayList<>();
        messageEntities.forEach(messageEntity -> messageIds.add(messageEntity.getId()));
        // 获取该用户所有讯息对应的点赞数据（含用户自己，发送notification的功能也含了用户自己在内）
        List<LikeEntity> likeEntities = new ArrayList<>();
        messageEntities.forEach(messageEntity -> {
            // 根据一讯息的id获取其点赞数据,该数据需要已发送且为like
            List<LikeEntity> tmpLikeEntities = likeRepository.findByIdMessageIdAndStatusAndLikeStatus(
                    messageEntity.getId(), MessageOperateStatus.sent,LikeStatus.like);
            // 有数据,则全部装入
            if(!CollectionUtil.isEmpty(tmpLikeEntities))
                likeEntities.addAll(tmpLikeEntities);

        });
        // 若该用户的所有讯息均无点赞数据，返回null
        if (CollectionUtil.isEmpty(likeEntities))
            return null;
        // 记录userId
        List<String> userIds = new ArrayList<>();
        likeEntities.forEach(likeEntity -> userIds.add(likeEntity.getId().getUserId()));
        // 获取userInfoDtos
        List<UserInfoDto> userInfoDtos = userClient.getUserInfoDtos(userIds);
        // 获取messageInfoDto,并组装LikeDto,放置进入likeDtos
        List<LikeDto> likeDtos = new ArrayList<>();
        for(LikeEntity entity:likeEntities){
            int idx = likeEntities.indexOf(entity);
            UserInfoDto userInfoDto = userInfoDtos.get(idx);
            Long messageId = entity.getId().getMessageId();
            int msgIdx = messageIds.indexOf(messageId);
            MessageEntity msgEntity = messageEntities.get(msgIdx);
            MessageDto messageDto = messageConvertor.convertToDTO(msgEntity);
            LikeDto likeDto = LikeDto.builder().userInfoDto(userInfoDto).messageDto(messageDto).createAt(entity.getCreateAt()).build();
            likeDtos.add(likeDto);
        }
        Comparator timeComparator = new Comparator<LikeDto>(){
            @Override
            public int compare(LikeDto o1, LikeDto o2) {
                Date d1 = o1.getCreateAt();
                Date d2 = o2.getCreateAt();
                if(d1.getTime()>d2.getTime())
                    return -1;
                else if(d1.getTime()<d2.getTime())
                    return 1;
                else
                    return 0;
            }
        };
        likeDtos.sort(timeComparator);
        return likeDtos;
    }


}
