package com.focus.focus.user.controller;

import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.user.service.IFriendshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/friend")
public class FriendshipController {
    @Autowired
    private IFriendshipService friendshipService;

    @GetMapping("/getFollowStatus")
    public ResponseEntity<ResponseMsg> getFollowStatus(@RequestParam("sourceId") String sourceId,
                                                       @RequestParam("targetId") String targetId){
        Boolean friendshipStatus = friendshipService.getFriendshipStatus(sourceId, targetId);
        if(!friendshipStatus)
            return  ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_FOLLOW_STATUS_FALSE.getCode(),
                    ResponseCode.USER_FOLLOW_STATUS_FALSE.getMsg(),false));
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_FOLLOW_STATUS_TRUE.getCode(),
                ResponseCode.USER_FOLLOW_STATUS_TRUE.getMsg(),true));
    }

    @PostMapping("/followUser")
    public ResponseEntity<ResponseMsg> followUser(@RequestParam("sourceId") String sourceId,
                                                  @RequestParam("targetId") String targetId,
                                                  @RequestParam("date") Long date){
        Boolean followStatus = friendshipService.followUser(sourceId, targetId, new Date(date));
        if(!followStatus)
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_FOLLOW_ERROR.getCode(),
                    ResponseCode.USER_FOLLOW_ERROR.getMsg(),false));
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_FOLLOW_SUCCESS.getCode(),
                ResponseCode.USER_FOLLOW_SUCCESS.getMsg(),true));
    }

    @PostMapping("/unFollowUser")
    public ResponseEntity<ResponseMsg> unFollowUser(@RequestParam("sourceId") String sourceId,
                                                    @RequestParam("targetId") String targetId,
                                                    @RequestParam("date") Long date){
        Boolean unFollowStatus = friendshipService.unFollowUser(sourceId, targetId, new Date(date));
        if(!unFollowStatus)
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UNFOLLOW_ERROR.getCode(),
                    ResponseCode.USER_UNFOLLOW_ERROR.getMsg(),false));
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UNFOLLOW_SUCCESS.getCode(),
                ResponseCode.USER_UNFOLLOW_SUCCESS.getMsg(),true));
    }
}
