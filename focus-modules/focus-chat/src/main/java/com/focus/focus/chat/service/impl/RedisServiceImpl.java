package com.focus.focus.chat.service.impl;

import com.focus.focus.api.dto.ChatDto;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.chat.convertor.ChatConvertor;
import com.focus.focus.chat.dao.ChatRepository;
import com.focus.focus.chat.domain.entity.ChatEntity;
import com.focus.focus.chat.service.IRedisService;
import com.focus.redis.common.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements IRedisService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private ChatConvertor chatConvertor;
    private final UserClient userClient;

    // 将MySQL中的该用户的聊天对象同步到Redis中,zset(chatZSet:userId,chatId,timestamp)
    @Override
    public Boolean transChatZSetToRedis(String userId) {
        // 将该用户的聊天记录全部获取，以取得该用户的聊天集合
        List<ChatEntity> sourceEntities = chatRepository.findByIdSourceId(userId);
        List<ChatEntity> targetEntities = chatRepository.findByIdTargetId(userId);
        // 若数据库内无用户聊天数据，则无法构造出聊天集合，即没有聊天集合
        if((sourceEntities==null||sourceEntities.size()==0)&&(targetEntities==null||targetEntities.size()==0))
            return false;
        // 拼接该userId的聊天集合的ZSET KEY
        String chatZSetKey = RedisKeyUtil.ZSET_KEY_CHAT_ZSET + userId;
        // 将用户曾聊过天的对象加入Redis聊天集合中去，
        if(sourceEntities!=null && sourceEntities.size()!=0){
            sourceEntities.forEach(e->{
                // 充当score
                Long timeStamp = e.getCreateAt().getTime();
                // 获取聊天用户的信息
                String chatUserId = e.getId().getTargetId();
                // 将分值最高的即日期最新的放入聊天集合中
                Double oldTimeStamp;
                if((oldTimeStamp = redisTemplate.opsForZSet().score(chatZSetKey,chatUserId))!=null){
                    if(timeStamp > oldTimeStamp)
                        // 二者对话有更新的消息，更新之
                        redisTemplate.opsForZSet().add(chatZSetKey,chatUserId,timeStamp);
                }
                else{
                    redisTemplate.opsForZSet().add(chatZSetKey,chatUserId,timeStamp);
                }
            });
        }
        if(targetEntities!=null && targetEntities.size()!=0){
            targetEntities.forEach(e->{
                // 时间戳充当分数
                Long timeStamp = e.getCreateAt().getTime();
                // 获取与userId的聊天用户的Id
                String chatUserId = e.getId().getSourceId();
                // 更新分值最高的即日期最新的于聊天集合中
                Double oldTimeStamp;
                if((oldTimeStamp = redisTemplate.opsForZSet().score(chatZSetKey,chatUserId))!=null){
                    if(timeStamp > oldTimeStamp)
                        redisTemplate.opsForZSet().add(chatZSetKey,chatUserId,timeStamp);
                }
                else{
                    redisTemplate.opsForZSet().add(chatZSetKey,chatUserId,timeStamp);
                }
            });
        }
        return true;
    }

    // 获取Redis中存储的ChatZSet，转换成List<ChatDto>返回
    @Override
    public List<ChatDto> getChatZSetFromRedis(String userId) {
        // 拼接key
        String chatZSetKey = RedisKeyUtil.ZSET_KEY_CHAT_ZSET + userId;
        // 不存在该key返回null
        if(!Objects.equals(redisTemplate.hasKey(chatZSetKey), Boolean.TRUE))
            return null;
        // 获取Redis中存储的zset
        Set<ZSetOperations.TypedTuple<Object>> zset = redisTemplate.opsForZSet().reverseRangeWithScores(chatZSetKey, 0, -1);
        Iterator<ZSetOperations.TypedTuple<Object>> it = zset.iterator();
        List<ChatDto> chatDtos = new ArrayList<>();
        while(it.hasNext()){
            ZSetOperations.TypedTuple<Object> next = it.next();
            ChatDto chatDto = ChatDto.builder()
                    .targetUser(userClient.getUserInfoDtoById((String)next.getValue()))
                    .createAt(new Date(next.getScore().longValue())).build();
            chatDtos.add(chatDto);
        }
        return chatDtos;
    }

    // userId是当前用户，talkId是聊天的用户。将历史聊天记录放进Redis
    @Override
    public Boolean transChatHistoryToRedis(String userId, String talkId) {
        // sourceId=userId,targetId=talkId;
        List<ChatEntity> userIdEqSourceId = chatRepository.findByIdSourceIdAndIdTargetId(userId, talkId);
        // sourceId=talkId,targetId=userId;
        List<ChatEntity> userIdEqTargetId = chatRepository.findByIdSourceIdAndIdTargetId(talkId, userId);
        // 无聊天记录则返回false
        if((userIdEqSourceId==null||userIdEqSourceId.size()==0)&&(userIdEqTargetId==null||userIdEqTargetId.size()==0))
            return false;
        // 历史聊天消息的key
        String chatHistoryKey = RedisKeyUtil.LIST_KEY_CHAT_HISTORY + userId;
        // 合并聊天记录
        List<ChatEntity> chatHistories = new ArrayList<>();
        if(userIdEqSourceId!=null&&userIdEqSourceId.size()!=0)
            chatHistories.addAll(userIdEqSourceId);
        if(userIdEqTargetId!=null&&userIdEqTargetId.size()!=0)
            chatHistories.addAll(userIdEqTargetId);
        // 升序比较器
        Comparator<ChatEntity> timeComparator = (o1,o2) -> {
                Date d1 = o1.getCreateAt();
                Date d2 = o2.getCreateAt();
                if(d1.getTime()>d2.getTime())
                    return 1;
                else if(d1.getTime()<d2.getTime())
                    return -1;
                else
                    return 0;
        };
        chatHistories.sort(timeComparator);
        chatHistories.forEach(e->{ redisTemplate.opsForList().rightPush(chatHistoryKey,e); });
        return true;
    }

    // 获取redis中的历史聊天记录，转化成List<ChatDto>返回
    @Override
    public List<ChatDto> getChatHistoryFromRedis(String userId) {
        // 拼接ChatHistoryKey
        String chatHistoryKey = RedisKeyUtil.LIST_KEY_CHAT_HISTORY + userId;
        // 不存在该key返回null
        if(!Objects.equals(redisTemplate.hasKey(chatHistoryKey),Boolean.TRUE))
            return null;
        // 获取Redis里面的ChatHistory
        List<Object> objects = redisTemplate.opsForList().range(chatHistoryKey, 0, -1);
        List<ChatEntity> chatEntities = objects.stream().map(o -> (ChatEntity) o).collect(Collectors.toList());
        List<ChatDto> chatDtos = (List<ChatDto>) chatConvertor.convertToDTOList(chatEntities);
        return chatDtos;
    }


    // 更新聊天集合ChatZSet
    @Override
    public void updateChatZSetInRedis(String userId, String chatUserId, Date date) {
        String chatZSet = RedisKeyUtil.ZSET_KEY_CHAT_ZSET + userId;
        Long timestamp = date.getTime();
        redisTemplate.opsForZSet().add(chatZSet,chatUserId,timestamp);
    }

    // 更新历史聊天记录ChatHistory
    @Override
    public void updateChatHistoryInRedis(String userId, ChatDto chatDto) {
        String chatHistoryKey = RedisKeyUtil.LIST_KEY_CHAT_HISTORY + userId;
        ChatEntity chatEntity = chatConvertor.convertToEntity(chatDto);
        redisTemplate.opsForList().rightPush(chatHistoryKey,chatEntity);
    }

    @Override
    public Boolean existZSetKeyAndNotEqZero(String key) {
        if(!Objects.equals(redisTemplate.hasKey(key),Boolean.TRUE))
            return false;
        Long chatZSetSize = redisTemplate.opsForZSet().size(key);
        if(Objects.equals(chatZSetSize,0L))
            return false;
        return true;
    }

    @Override
    public Boolean existListKeyAndNotEqZero(String key) {
        // 不存在该key
        if(!Objects.equals(redisTemplate.hasKey(key), Boolean.TRUE))
            return false;
        Long chatHistorySize = redisTemplate.opsForList().size(key);
        // 该key的记录为0条
        if(Objects.equals(chatHistorySize,0L))
            return false;
        return true;
    }


}
