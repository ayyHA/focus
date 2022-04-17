package com.focus.focus.search.service.impl;

import com.focus.auth.common.utils.OauthUtils;
import com.focus.focus.api.dto.MessageInfoDto;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.api.feign.MessageClient;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.api.util.LoginVal;
import com.focus.focus.search.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

//import com.focus.auth.common.model.LoginVal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements ISearchService {
    private final UserClient userClient;
    private final MessageClient messageClient;

    @Override
    public List<UserInfoDto> searchByNickname(String nickname) {
        // feign远程调用
        List<UserInfoDto> userInfoDtos = userClient.searchByNickname(nickname);
        return userInfoDtos;
    }

    @Override
    public List<MessageInfoDto> searchByKeywords(String keywords) {
        // feign远程调用[需传个LoginVal过去]
        LoginVal loginVal = OauthUtils.getCurrentUser();
        List<MessageInfoDto> messageInfoDtos = messageClient.searchByKeywords(keywords,loginVal);
        return messageInfoDtos;
    }
}
