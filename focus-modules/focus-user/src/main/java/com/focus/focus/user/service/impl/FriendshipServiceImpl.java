package com.focus.focus.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.focus.focus.api.enumerate.FollowStatus;
import com.focus.focus.user.dao.FriendshipRepository;
import com.focus.focus.user.domain.entity.FriendshipEntity;
import com.focus.focus.user.service.IFriendshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class FriendshipServiceImpl implements IFriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public Boolean getFriendshipStatus(String sourceId, String targetId) {
        FriendshipEntity.FriendshipId id = new FriendshipEntity.FriendshipId(sourceId,targetId);
        Optional<FriendshipEntity> opEntity = friendshipRepository.findById(id);
        if(opEntity.isPresent()){
            FriendshipEntity entity = opEntity.get();
            FollowStatus followStatus = entity.getFollowStatus();
            if(followStatus==FollowStatus.follow)
                return true;
            else
                return false;
        }
        return false;
    }

    @Override
    public Boolean followUser(String sourceId, String targetId, Date date) {
        FriendshipEntity.FriendshipId id = new FriendshipEntity.FriendshipId(sourceId,targetId);
        FriendshipEntity entity = FriendshipEntity.builder().id(id).followDate(date).followStatus(FollowStatus.follow).build();
        FriendshipEntity saveEntity = friendshipRepository.save(entity);
        return !ObjectUtil.isEmpty(saveEntity);
    }

    @Override
    public Boolean unFollowUser(String sourceId, String targetId,Date date) {
        FriendshipEntity.FriendshipId id = new FriendshipEntity.FriendshipId(sourceId,targetId);
        Optional<FriendshipEntity> opEntity = friendshipRepository.findById(id);
        if(opEntity.isPresent()){
            FriendshipEntity entity = opEntity.get();
            entity.setFollowDate(date);
            entity.setFollowStatus(FollowStatus.unfollow);
            friendshipRepository.save(entity);
            return true;
        }
        return false;
    }
}
