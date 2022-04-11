package com.focus.focus.message.service;

import com.focus.focus.message.domain.entity.LikeEntity;

import java.util.List;

public interface ILikeService {
    LikeEntity save(LikeEntity likeEntity);
    List<LikeEntity> saveAll(List<LikeEntity> likeEntities);
    void transLikeFromRedisToDB();
}
