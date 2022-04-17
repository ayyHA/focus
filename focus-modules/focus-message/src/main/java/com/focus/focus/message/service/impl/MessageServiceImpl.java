package com.focus.focus.message.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focus.auth.common.utils.OauthUtils;
import com.focus.focus.api.dto.*;
import com.focus.focus.api.enumerate.LikeStatus;
import com.focus.focus.api.enumerate.MessageTypeEnum;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.api.util.LoginVal;
import com.focus.focus.message.convertor.MessageConvertor;
import com.focus.focus.message.convertor.MessagePublicDataConvertor;
import com.focus.focus.message.dao.LikeRepository;
import com.focus.focus.message.dao.MessagePublicDataRepository;
import com.focus.focus.message.dao.MessageRepository;
import com.focus.focus.message.domain.entity.LikeEntity;
import com.focus.focus.message.domain.entity.MessageEntity;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import com.focus.focus.message.service.IMessageService;
import com.focus.focus.message.service.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import com.focus.auth.common.model.LoginVal;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private MessageConvertor messageConvertor;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessagePublicDataConvertor messagePublicDataConvertor;
    @Autowired
    private MessagePublicDataRepository publicDataRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private IRedisService redisService;
    // 远程调用User模块的方法
    private final UserClient userClient;

    @Override
    public Boolean save(MessageDto messageDto) {
       // 类型转换
       MessageEntity message = messageConvertor.convertToEntity(messageDto);
       // message的类型为回复，但没有指向回复的讯息的作者ID或是讯息ID
       if(message.getType() == MessageTypeEnum.replied_to &&
               (message.getConversationId()==null ||
                message.getInReplyToAuthorId()==null||
                message.getInReplyToAuthorId().equals(""))){
           return false;
       }
       // 持久化
       MessageEntity save = messageRepository.save(message);
       if(ObjectUtil.isEmpty(save))
           return false;
       return true;
    }

    @Override
    public List<MessageInfoDto> showMessage(Integer page) {
        // 分页查询，一页5条讯息 page->offset,size->limit
        PageRequest pageRequest = PageRequest.of(page,2);
        Page<MessageEntity> pageOfMessages = messageRepository.findAll(pageRequest);
        List<MessageEntity> messageList = pageOfMessages.getContent();
        // 设置最大页数和当前message总数
        int maxPages = pageOfMessages.getTotalPages();
        long maxElements = pageOfMessages.getTotalElements();
        // 将上述的MessageEntities转换为MessageDtos
        List<MessageDto> messageDtos = (List<MessageDto>) messageConvertor.convertToDTOList(messageList);
        // 获取当前用户的信息
        LoginVal currentUser = OauthUtils.getCurrentUser();
        // 获取当前用户的ID(远程获取)
        UserInfoDto currentUserInfoDto = userClient.getUserInfoDto(currentUser.getUsername());
        // 获取ids(message的),获取authorIds(user的),获取operateIds(messageOperate的)
        List<Long> ids = new ArrayList<>();
        List<String> authorIds = new ArrayList<>();
        List<LikeEntity.LikeId> likeIds = new ArrayList<>();
        messageList.forEach(message->{
            ids.add(message.getId());
            authorIds.add(message.getAuthorId());
            likeIds.add(new LikeEntity.LikeId(currentUserInfoDto.getId(),message.getId()));
        });
        // 获取MessagePublicDataDtos
        List<MessagePublicDataDto> publicDataDtos = getMessagePublicDataDtos(ids);
        // 获取UserInfoDtos(远程获取)
        List<UserInfoDto> userInfoDtos = userClient.getUserInfoDtos(authorIds);
        // 获取MessageStatusDtos
        List<MessageStatusDto> statusDtos = getMessageStatusDtos(likeIds);
        // 组装MessageInfoDtos
        List<MessageInfoDto> messageInfoDtoList = new ArrayList<>();
        ids.forEach(id ->{
            int idx = ids.indexOf(id);
            MessageInfoDto messageInfoDto = new MessageInfoDto();
            messageInfoDto.setMessageDto(messageDtos.get(idx));
            messageInfoDto.setMessagePublicDataDto(publicDataDtos.get(idx));
            messageInfoDto.setUserInfoDto(userInfoDtos.get(idx));
            messageInfoDto.setMessageStatusDto(statusDtos.get(idx));
            messageInfoDto.setMaxElements(maxElements);
            messageInfoDto.setMaxPages(maxPages);
            messageInfoDtoList.add(messageInfoDto);
        });
        return messageInfoDtoList;
    }

    @Override
    public List<MessageDto> searchByKeywords(String keywords) {
        if(StringUtils.isEmpty(keywords))
            return null;
        // 模糊匹配
        List<MessageEntity> messageEntities = messageRepository.findByKeywordsContaining(keywords);
        // 判定是否为空
        if(CollectionUtil.isEmpty(messageEntities))
            return null;
        // 非空则类型转换并返回
        List<MessageDto> messageDtos = (List<MessageDto>) messageConvertor.convertToDTOList(messageEntities);
        return messageDtos;
    }

    // 获取MessageStatusDtos
    private List<MessageStatusDto> getMessageStatusDtos(List<LikeEntity.LikeId> likeIds){
        List<MessageStatusDto> statusDtos = new ArrayList<>();
        likeIds.forEach(likeId ->{
            MessageStatusDto statusDto = new MessageStatusDto();
            Optional<LikeEntity> opLike = likeRepository.findById(likeId);
            if(opLike.isPresent()) {
                LikeEntity likeEntity = opLike.get();
                LikeStatus likeStatus = likeEntity.getLikeStatus();
                if(likeStatus==LikeStatus.like)
                    statusDto.setLikeStatus(true);
                else
                    statusDto.setLikeStatus(false);
                // 将点赞数据写入redis中（注：要本来就存在于数据库中，即你得点赞/取消点赞过）
                redisService.transLikeToRedis(likeEntity);
            }
            else
                statusDto.setLikeStatus(false);
            statusDto.setRetweetStatus(false); // 后面增加
            statusDtos.add(statusDto);
        });
        return statusDtos;
    }
    // 获取MessagePublicDataDtos
    private List<MessagePublicDataDto> getMessagePublicDataDtos(List<Long> ids){
        List<MessagePublicDataEntity> publicDataEntities = publicDataRepository.findAllById(ids);
        List<MessagePublicDataDto> publicDataDtos =
                (List<MessagePublicDataDto>) messagePublicDataConvertor.convertToDTOList(publicDataEntities);
        // 将公共数据写入redis中
        publicDataEntities.forEach(publicDataEntity -> {
            redisService.transLikeCountToRedis(publicDataEntity);
        });
        return publicDataDtos;
    }

    // 搜索调用的获取MessageInfoDtos[messageDto,messagePublicDto,userInfoDto,messageStatusDto]
    @Override
    public List<MessageInfoDto> getMsgInfoDtos(List<MessageDto> messageDtos,LoginVal loginVal){
        List<Long> msgIds = new ArrayList<>();
        List<String> authorIds = new ArrayList<>();
        List<LikeEntity.LikeId> likeIds = new ArrayList<>();
//        LoginVal loginVal = OauthUtils.getCurrentUser();
        UserInfoDto currentUser = userClient.getUserInfoDto(loginVal.getUsername());
        messageDtos.forEach( m ->{
            msgIds.add(m.getId());
            authorIds.add(m.getAuthorId());
            likeIds.add(new LikeEntity.LikeId(currentUser.getId(),m.getId()));
        });
        // 获取messagePublicDataDto
        List<MessagePublicDataDto> publicDataDtos = getMessagePublicDataDtos(msgIds);
        // 获取userInfoDto
        List<UserInfoDto> userInfoDtos = userClient.getUserInfoDtos(authorIds);
        // 获取messageStatusDto
        List<MessageStatusDto> statusDtos = getMessageStatusDtos(likeIds);
        // 组装messageInfoDto
        List<MessageInfoDto> messageInfoDtos = new ArrayList<>();
        msgIds.forEach( msgId ->{
            int idx = msgIds.indexOf(msgId);
            MessageInfoDto infoDto = MessageInfoDto.builder()
                    .messageDto(messageDtos.get(idx))
                    .messagePublicDataDto(publicDataDtos.get(idx))
                    .userInfoDto(userInfoDtos.get(idx))
                    .messageStatusDto(statusDtos.get(idx)).build();
            messageInfoDtos.add(infoDto);
        });
        return messageInfoDtos;
    }
}
