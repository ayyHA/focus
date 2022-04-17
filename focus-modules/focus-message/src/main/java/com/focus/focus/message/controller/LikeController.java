package com.focus.focus.message.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.focus.focus.api.dto.LikeDto;
import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.message.service.ILikeService;
import com.focus.focus.message.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private IRedisService redisService;
    @Autowired
    private ILikeService likeService;

    @PostMapping("/saveLike")
    public ResponseEntity<ResponseMsg> saveLike(@RequestParam("userId") String userId,
                                                @RequestParam("messageId") Long messageId,
                                                @RequestParam("likeCount") Long likeCount){
        log.info("Coming saveLike");
        redisService.saveLikeToRedis(messageId,userId);
        Long newLikeCount = redisService.incrementLikeCount(messageId, likeCount);
        Map<String,Object> data = new HashMap<>();
        data.put("likeCount",newLikeCount);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_LIKE_SUCCESS.getCode(),
                ResponseCode.MESSAGE_LIKE_SUCCESS.getMsg(),data));
    }

    @PostMapping("/saveUnlike")
    public ResponseEntity<ResponseMsg> saveUnlike(@RequestParam("userId") String userId,
                                                  @RequestParam("messageId") Long messageId,
                                                  @RequestParam("likeCount") Long likeCount){
        log.info("Coming saveUnlike");
        redisService.saveUnlikeToRedis(messageId, userId);
        Long newLikeCount = redisService.decrementLikeCount(messageId, likeCount);
        Map<String,Object> data = new HashMap<>();
        data.put("likeCount",newLikeCount);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_UNLIKE_SUCCESS.getCode(),
                ResponseCode.MESSAGE_UNLIKE_SUCCESS.getMsg(),data));
    }

    // 站内信中显示具体的点赞信息
    @GetMapping("/getLikeData/{userId}")
    public ResponseEntity<ResponseMsg> getLikeData(@PathVariable("userId") String userId){
        List<LikeDto> likeDtos = likeService.getLikeData(userId);
        if(CollectionUtil.isEmpty(likeDtos)){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_SHOW_SUCCESS.getCode(),
                    ResponseCode.MESSAGE_SHOW_SUCCESS.getMsg(),null));
        }
        Map<String,List<LikeDto>> data = new HashMap<>();
        data.put("likedtos",likeDtos);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_SHOW_SUCCESS.getCode(),
                ResponseCode.MESSAGE_SHOW_SUCCESS.getMsg(),data));
    }
}
