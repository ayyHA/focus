package com.focus.focus.chat.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.focus.focus.api.dto.ChatDto;
import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.chat.domain.entity.ChatEntity;
import com.focus.focus.chat.service.IChatService;
import com.focus.focus.chat.service.IRedisService;
import com.focus.redis.common.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// 进入Chat页即为其home页，传入当前使用者的userId
@Slf4j
@RestController
@ServerEndpoint("/home/{userId}")
public class ChatController {
    @Autowired
    private IRedisService redisService;
    @Autowired
    private IChatService chatService;
    // 在线用户数
    private static AtomicInteger onlineUserCount = new AtomicInteger(0);
    // 在线用户Map<userId,this>
    private static Map<String,ChatController> onlineUserMap = new ConcurrentHashMap<>();
    // {userId}对应的session
    private Session session;
    // {userId}
    private String userId;

    // 建立连接
    @OnOpen
    public void onOpen(@PathParam("userId")String userId,Session session){
        // 建立连接，将客户端信息进行记录
        this.session = session;
        this.userId = userId;
        onlineUserCount.incrementAndGet();
        onlineUserMap.put(userId,this);
        // 当Redis中聊天集合不存在或无数据时,去MySQL读数据过来
        String chatZSetKey = RedisKeyUtil.ZSET_KEY_CHAT_ZSET + userId;
        if(!redisService.existZSetKeyAndNotEqZero(chatZSetKey)) {
            // 将ChatZSet同步到redis中 /*loadStatus:true-数据装载成功 false-无数据*/
            redisService.transChatZSetToRedis(userId);
        }
    }

    // 关闭连接
    @OnClose
    public void onClose(){
        onlineUserMap.remove(userId);
        onlineUserCount.decrementAndGet();
    }

    // 收到客户端的消息
    @OnMessage
    public void onMessage(String message,Session session,@PathParam("userId") String userId){
        ChatEntity chatEntity = JSONObject.parseObject(message, ChatEntity.class);
        send(chatEntity,userId);
    }

    // 异常调用
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }

    // 从redis读，获取聊天集合ChatZSet
    @GetMapping("/getChatZSet")
    public ResponseEntity<ResponseMsg> getChatZSet(@RequestParam("userId") String userId){
        List<ChatDto> chatDtos = redisService.getChatZSetFromRedis(userId);
        if(CollectionUtil.isEmpty(chatDtos))
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.CHAT_ZSET_ERROR.getCode(),
                    ResponseCode.CHAT_ZSET_ERROR.getMsg(),null));
        Map<String,List<ChatDto>> data = new HashMap<>();
        data.put("chatDtos",chatDtos);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.CHAT_ZSET_SUCCESS.getCode(),
                ResponseCode.CHAT_ZSET_SUCCESS.getMsg(),data));
    }

    // 获取历史聊天记录，redis有无数据，无则同步后读，有则读
    @GetMapping("/getChatHistory")
    public ResponseEntity<ResponseMsg> getChatHistory(@RequestParam("userId") String userId,
                                                      @RequestParam("talkId") String talkId){
        String chatHistoryKey = RedisKeyUtil.LIST_KEY_CHAT_HISTORY + userId;
        // 为空或数据为零则同步下数据
        if(!redisService.existListKeyAndNotEqZero(chatHistoryKey)){
            redisService.transChatHistoryToRedis(userId,talkId);
        }
        // 从redis中读取数据
        List<ChatDto> chatDtos = redisService.getChatHistoryFromRedis(userId);
        if(CollectionUtil.isEmpty(chatDtos))
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.CHAT_HISTORY_ERROR.getCode(),
                    ResponseCode.CHAT_HISTORY_ERROR.getMsg(),null));
        Map<String,List<ChatDto>> data = new HashMap<>();
        data.put("chatDtos",chatDtos);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.CHAT_HISTORY_SUCCESS.getCode(),
                ResponseCode.CHAT_HISTORY_SUCCESS.getMsg(),data));
    }

    // 发送消息给客户端
    private void send(ChatEntity chatEntity,String userId){
        String sourceId = chatEntity.getId().getSourceId();
        String targetId = chatEntity.getId().getTargetId();
        // 持久化
        ChatDto chatDto = chatService.updateChatEntity(chatEntity);
        // 获取chatUserId
        String chatUserId = userId.equals(sourceId) ?targetId:sourceId;
        // 更新redis
        redisService.updateChatZSetInRedis(userId,chatUserId,chatEntity.getCreateAt());
        redisService.updateChatHistoryInRedis(userId,chatDto);
        // 将Object变成JSON字符串
        String JSONStr = JSON.toJSONString(chatDto);
        // 获取Controller以取得对应的session
        ChatController sourceController = onlineUserMap.get(sourceId);
        ChatController targetController = onlineUserMap.get(targetId);
        // 给发送消息的用户推送
        if(sourceController!=null){
            Session session = sourceController.session;
            asyncSend(session,JSONStr);
        }
        // 给接收消息的用户推送
        if(targetController!=null){{
            Session session = targetController.session;
            asyncSend(session,JSONStr);
        }}
    }

    // 异步发送消息
    private void asyncSend(Session session,String message){
        session.getAsyncRemote().sendText(message);
    }
}
