package com.focus.focus.message.service.impl;

import com.focus.focus.api.enumerate.LikeStatus;
import com.focus.focus.api.enumerate.MessageOperateStatus;
import com.focus.focus.message.domain.entity.LikeEntity;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import com.focus.focus.message.service.IRedisService;
import com.focus.redis.common.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 1->like
    @Override
    public void saveLikeToRedis(Long messageId, String userId) {
        String messageUserId = RedisKeyUtil.linkedId(messageId,userId);
        redisTemplate.opsForHash().put(RedisKeyUtil.HASH_KEY_LIKE,messageUserId,1);
    }

    // 0->unlike
    @Override
    public void saveUnlikeToRedis(Long messageId, String userId) {
        String messageUserId = RedisKeyUtil.linkedId(messageId,userId);
        redisTemplate.opsForHash().put(RedisKeyUtil.HASH_KEY_LIKE,messageUserId,0);
    }

    @Override
    public void deleteLikeInRedis(Long messageId, String userId) {
        String messageUserId = RedisKeyUtil.linkedId(messageId,userId);
        redisTemplate.opsForHash().delete(RedisKeyUtil.HASH_KEY_LIKE,messageUserId);
    }

    // 自增自减数据需注意前后端数据同步，以防止提交后的数据再次更新不准确
    // redis对hk的序列化规则更改为String，故需要cast
    @Override
    public Long incrementLikeCount(Long messageId,Long likeCount) {
        String msgId = String.valueOf(messageId);
        if(redisTemplate.opsForHash().get(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId)==null){
            redisTemplate.opsForHash().put(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId,likeCount);
        }
        redisTemplate.opsForHash().increment(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId,1L);
        Integer lcnt = (Integer) redisTemplate.opsForHash().get(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId);
        return lcnt.longValue();
    }

    @Override
    public Long decrementLikeCount(Long messageId,Long likeCount) {
        String msgId = String.valueOf(messageId);
        if(redisTemplate.opsForHash().get(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId)==null){
            redisTemplate.opsForHash().put(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId,likeCount);
        }
        redisTemplate.opsForHash().increment(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId,-1L);
        Integer lcnt = (Integer) redisTemplate.opsForHash().get(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId);
        return lcnt.longValue();
    }

    @Override
    public List<LikeEntity> getAllLikeInRedis() {
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisKeyUtil.HASH_KEY_LIKE, ScanOptions.NONE);
        List<LikeEntity> likeEntities = new ArrayList<>();
        while(cursor.hasNext()){
            Map.Entry<Object, Object> entry = cursor.next();
            String messageUserId = (String)entry.getKey();
            String[] split = messageUserId.split("::");
            Long messageId = Long.parseLong(split[0]);
            String userId = split[1];
            Integer status = (Integer)entry.getValue();
            LikeStatus likeStatus = (status==1) ? LikeStatus.like: LikeStatus.unlike;
            LikeEntity likeEntity =  LikeEntity.builder()
                    .id(new LikeEntity.LikeId(userId, messageId))
                    .status(MessageOperateStatus.unsent)
                    .likeStatus(likeStatus).build();
            likeEntities.add(likeEntity);
            // 存入list后从redis删除
            redisTemplate.opsForHash().delete(RedisKeyUtil.HASH_KEY_LIKE,messageUserId);
        }
        return likeEntities;
    }

    @Override
    public List<MessagePublicDataEntity> getAllLikeCountInRedis() {
        Cursor<Map.Entry<Object,Object>> cursor = redisTemplate.opsForHash().scan(RedisKeyUtil.HASH_KEY_LIKE_COUNT, ScanOptions.NONE);
        List<MessagePublicDataEntity> publicDataEntities = new ArrayList<>();
        while (cursor.hasNext()){
            Map.Entry<Object, Object> entry = cursor.next();
            String msgId = (String) entry.getKey();
            Long messageId = Long.parseLong(msgId);
            Integer likeCnt = (Integer) entry.getValue();
            Long likeCount = likeCnt.longValue();
            MessagePublicDataEntity publicDataEntity = MessagePublicDataEntity.builder()
                    .messageId(messageId)
                    .likeCount(likeCount).build();
            publicDataEntities.add(publicDataEntity);
            // 存入list后从redis删除
            redisTemplate.opsForHash().delete(RedisKeyUtil.HASH_KEY_LIKE_COUNT,msgId);
        }
        return publicDataEntities;
    }

    // 将DB的数据导入Redis [1::400...,1] [messageId::userId,status(0unlike/1like)]
    @Override
    public void transLikeToRedis(LikeEntity likeEntity) {
        LikeEntity.LikeId id = likeEntity.getId();
        String messageUserId = RedisKeyUtil.linkedId(id.getMessageId(),id.getUserId());
        LikeStatus likeStatus = likeEntity.getLikeStatus();
        Integer status = likeStatus ==LikeStatus.like ? 1 : 0 ;
        redisTemplate.opsForHash().put(RedisKeyUtil.HASH_KEY_LIKE,messageUserId,status);
    }

    // 将DB的数据导入Redis [1,1] [messageId,likeCount]
    @Override
    public void transLikeCountToRedis(MessagePublicDataEntity publicDataEntity) {
        Long messageId = publicDataEntity.getMessageId();
        Long likeCount = publicDataEntity.getLikeCount();
        redisTemplate.opsForHash().put(RedisKeyUtil.HASH_KEY_LIKE_COUNT,String.valueOf(messageId),likeCount);
    }
}
