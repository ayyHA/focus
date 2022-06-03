package com.focus.focus.user.service;

import com.focus.focus.api.dto.SignDto;
import com.focus.focus.api.dto.UserInfoDto;

import java.util.Date;
import java.util.List;

public interface IUserService {
    Boolean updateUserAvatar(String path);
    Boolean updateUserBackground(String path);
    UserInfoDto getUserInfoByUsername(String username);
    UserInfoDto getUserInfoById(String userId);
    UserInfoDto updateUserDetails(UserInfoDto dto);
    List<UserInfoDto> getUserInfoDTOs(List<String> ids);
    List<UserInfoDto> searchByNickname(String nickname);
    SignDto signFocus(String userId,Date date);
    Boolean getSignStatus(String userId,Date date);
    Long getPinnedMessageId(String userId);
    Boolean doReward(String sourceId,String targetId,Long amountOfCoin);
}
