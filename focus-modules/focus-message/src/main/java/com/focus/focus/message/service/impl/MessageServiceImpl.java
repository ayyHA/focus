package com.focus.focus.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.focus.auth.common.model.LoginVal;
import com.focus.auth.common.utils.OauthUtils;
import com.focus.focus.api.dto.*;
import com.focus.focus.api.enumerate.MessageTypeEnum;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.message.convertor.MessageConvertor;
import com.focus.focus.message.convertor.MessagePublicDataConvertor;
import com.focus.focus.message.dao.LikeRepository;
import com.focus.focus.message.dao.MessagePublicDataRepository;
import com.focus.focus.message.dao.MessageRepository;
import com.focus.focus.message.domain.entity.LikeEntity;
import com.focus.focus.message.domain.entity.MessageEntity;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import com.focus.focus.message.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<MessagePublicDataEntity> publicDataEntities = publicDataRepository.findAllById(ids);
        List<MessagePublicDataDto> publicDataDtos =
                (List<MessagePublicDataDto>) messagePublicDataConvertor.convertToDTOList(publicDataEntities);
        // 获取UserInfoDtos(远程获取)
        List<UserInfoDto> userInfoDtos = userClient.getUserInfoDtos(authorIds);
        // 获取MessageStatusDtos
        List<MessageStatusDto> statusDtos = new ArrayList<>();
        likeIds.forEach(likeId ->{
            MessageStatusDto statusDto = new MessageStatusDto();
            Optional<LikeEntity> opLike = likeRepository.findById(likeId);
            if(opLike.isPresent()) {
                statusDto.setLikeStatus(true);
            }
            else
                statusDto.setLikeStatus(false);
            statusDto.setRetweetStatus(false);
            statusDtos.add(statusDto);
        });
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
}
