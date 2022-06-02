package com.focus.focus.user.service;
import java.util.Date;

public interface IFriendshipService {
    // 获取用户间的关注状态
    Boolean getFriendshipStatus(String sourceId,String targetId);
    // 关注用户
    Boolean followUser(String sourceId, String targetId, Date date);
    // 取关用户
    Boolean unFollowUser(String sourceId,String targetId,Date date);
}
