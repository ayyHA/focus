package com.focus.focus.message.service;

import com.focus.focus.message.domain.entity.LikeEntity;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;

import java.util.List;

public interface IRedisService {
    // 点赞 status==1
    void saveLikeToRedis(Long messageId,String userId);
    // 取消点赞 status==0
    void saveUnlikeToRedis(Long messageId,String userId);
    // 删除点赞数据
    void deleteLikeInRedis(Long messageId,String userId);
    // 点赞数目+1
    Long incrementLikeCount(Long messageId,Long likeCount);
    // 点赞数目-1
    Long decrementLikeCount(Long messageId,Long likeCount);
    // 获得所有点赞数据
    List<LikeEntity> getAllLikeInRedis();
    // 获得所有点赞数目数据
    List<MessagePublicDataEntity> getAllLikeCountInRedis();
    // 将LikeEntity存入Redis
    void transLikeToRedis(LikeEntity likeEntity);
    // 将LikeCount存入Redis
    void transLikeCountToRedis(MessagePublicDataEntity publicDataEntity);
}
