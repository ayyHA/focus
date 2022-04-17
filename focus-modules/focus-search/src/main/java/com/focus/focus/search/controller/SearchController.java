package com.focus.focus.search.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.focus.focus.api.dto.MessageInfoDto;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
public class SearchController {
    @Autowired
    private ISearchService searchService;
    
    @GetMapping("/byKeywords")
    public ResponseEntity<ResponseMsg> searchByKeywords(@RequestParam("keywords") String keywords){
        List<MessageInfoDto> messageInfoDtos = searchService.searchByKeywords(keywords);
        // 匹配不到相关关键词(<==>匹配错误)，返回如下
        if(CollectionUtil.isEmpty(messageInfoDtos))
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.SEARCH_KEYWORDS_ERROR.getCode(),
                    ResponseCode.SEARCH_KEYWORDS_ERROR.getMsg(),null));
        // 匹配成功
        Map<String,List<MessageInfoDto>> data = new HashMap<>();
        data.put("messageInfoDtos",messageInfoDtos);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.SEARCH_KEYWORDS_SUCCESS.getCode(),
                ResponseCode.SEARCH_KEYWORDS_SUCCESS.getMsg(),data));
    }
    
    @GetMapping("/byNickname")
    public ResponseEntity<ResponseMsg> searchByNickname(@RequestParam("nickname") String nickname){
        List<UserInfoDto> userInfoDtos = searchService.searchByNickname(nickname);
        // 匹配不到相关的昵称等价于匹配错误，返回如下
        if(CollectionUtil.isEmpty(userInfoDtos))
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.SEARCH_NICKNAME_ERROR.getCode(),
                    ResponseCode.SEARCH_NICKNAME_ERROR.getMsg(),null));
        // 匹配成功
        Map<String,List<UserInfoDto>> data = new HashMap<>();
        data.put("userInfoDtos",userInfoDtos);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.SEARCH_NICKNAME_SUCCESS.getCode(),
                ResponseCode.SEARCH_NICKNAME_SUCCESS.getMsg(),data));
    }
}
