package com.focus.focus.search.service;

import com.focus.focus.api.dto.MessageInfoDto;
import com.focus.focus.api.dto.UserInfoDto;

import java.util.List;

public interface ISearchService {
    List<UserInfoDto> searchByNickname(String nickname);
    List<MessageInfoDto> searchByKeywords(String keywords);
}
