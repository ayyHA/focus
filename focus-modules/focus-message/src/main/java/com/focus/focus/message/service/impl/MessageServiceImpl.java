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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public MessageInfoDto save(MessageDto messageDto) {
       // 类型转换
       MessageEntity message = messageConvertor.convertToEntity(messageDto);
       // message的类型为回复，但没有指向回复的讯息的作者ID或是讯息ID
       if((message.getType() == MessageTypeEnum.replied_to ||
               message.getType() == MessageTypeEnum.retweeted)
               &&
               (message.getConversationId()==null ||
                message.getInReplyToAuthorId()==null||
                message.getInReplyToAuthorId().equals(""))){
           return null;
       }
       // 持久化
       MessageEntity save = messageRepository.save(message);
       if(ObjectUtil.isEmpty(save))
           return null;
       // 评论型消息则更新评论数【增加】
       if(save.getType()==MessageTypeEnum.replied_to)
           incrementReplyCount(save.getConversationId());
       // 转发型消息则更新转发数【增加】
       if(save.getType() == MessageTypeEnum.retweeted)
           incrementRetweetCount(save.getConversationId());
       // 获取messageInfoDto
        MessageInfoDto messageInfoDto = getMessageInfoDto(save);
        return messageInfoDto;
    }

    @Override
    public List<MessageInfoDto> showMessage(Integer page) {
        // 根据createAt进行倒序排序
        Sort sort = Sort.by(Sort.Direction.DESC,"createAt");
        // 分页查询，一页5条讯息 page->offset,size->limit
        PageRequest pageRequest = PageRequest.of(page,2,sort);
//        Page<MessageEntity> pageOfMessages = messageRepository.findAll(pageRequest);
        Page<MessageEntity> pageOfMessages = messageRepository.findByTypeNot(MessageTypeEnum.replied_to,pageRequest);
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
            statusDto.setLikeStatus(getLikeStatus(likeId.getUserId(),likeId.getMessageId()));
            statusDto.setRetweetStatus(getRetweetStatus(likeId.getUserId(),likeId.getMessageId()));
//            Optional<LikeEntity> opLike = likeRepository.findById(likeId);
//            if(opLike.isPresent()) {
//                LikeEntity likeEntity = opLike.get();
//                LikeStatus likeStatus = likeEntity.getLikeStatus();
//                if(likeStatus==LikeStatus.like)
//                    statusDto.setLikeStatus(true);
//                else
//                    statusDto.setLikeStatus(false);
//                // 将点赞数据写入redis中（注：要本来就存在于数据库中，即你得点赞/取消点赞过）
//                redisService.transLikeToRedis(likeEntity);
//            }
//            else
//                statusDto.setLikeStatus(false);
//            statusDto.setRetweetStatus(false); // 后面增加
            statusDtos.add(statusDto);
        });
        return statusDtos;
    }
    // 获取MessagePublicDataDtos
    private List<MessagePublicDataDto> getMessagePublicDataDtos(List<Long> ids){
        List<MessagePublicDataEntity> publicDataEntities = new ArrayList<>();
        ids.forEach(id->{
            Optional<MessagePublicDataEntity> opEntity = publicDataRepository.findById(id);
            opEntity.ifPresent(publicDataEntities::add);
        });
//        List<MessagePublicDataEntity> publicDataEntities = publicDataRepository.findAllByOrderByMessageIdDesc(ids);
        List<MessagePublicDataDto> publicDataDtos =
                (List<MessagePublicDataDto>) messagePublicDataConvertor.convertToDTOList(publicDataEntities);
        // 将公共数据写入redis中
//        publicDataEntities.forEach(publicDataEntity -> {
//            redisService.transLikeCountToRedis(publicDataEntity);
//        });
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

    // 根据消息发布者的Id和页号来获取对应的消息内容，可用于用户详情页和个人页
    @Override
    public List<MessageInfoDto> getMsgInfoDtosByAuthorId(String authorId, Integer pageNum) {
        // 获取对应用户的消息列表
        Page<MessageEntity> page = getPageByAuthorIdAndPageNum(authorId, pageNum);
        List<MessageEntity> messageEntities = page.getContent();
        // 获取其最大页数及message总数
        int maxPages = page.getTotalPages();
        long maxElements = page.getTotalElements();
        log.info("messageEntities: [{}]",messageEntities);
        List<MessageDto> messageDtos = (List<MessageDto>) messageConvertor.convertToDTOList(messageEntities);
        // 通过msgIds和likeIds获取MessagePublicDataDtos和MessageStatusDtos
        List<Long> msgIds = new ArrayList<>();
        List<LikeEntity.LikeId> likeIds = new ArrayList<>();
        // 获取当前用户的信息，便于后面判断其点赞状态
        LoginVal loginVal = OauthUtils.getCurrentUser();
        UserInfoDto currentUser = userClient.getUserInfoDto(loginVal.getUsername());
        // 组装id
        messageEntities.forEach(e->{
            msgIds.add(e.getId());
            likeIds.add(new LikeEntity.LikeId(currentUser.getId(),e.getId()));
        });
        log.info("msgIds: [{}]",msgIds.toString());
        // 获取messagePublicDataDtos
        List<MessagePublicDataDto> messagePublicDataDtos = getMessagePublicDataDtos(msgIds);
        log.info("publicDataDtos: [{}]",messagePublicDataDtos.toString());
        // 获取messageStatusDtos
        List<MessageStatusDto> messageStatusDtos = getMessageStatusDtos(likeIds);
        log.info("statusDtos: [{}]",messageStatusDtos.toString());
        // 获取作者信息
        UserInfoDto authorDto = userClient.getUserInfoDtoById(authorId);
        // 组装MessageInfoDtos
        List<MessageInfoDto> messageInfoDtos = new ArrayList<>();
        msgIds.forEach(id ->{
            int idx = msgIds.indexOf(id);
            messageInfoDtos.add(MessageInfoDto.builder()
                    .messageDto(messageDtos.get(idx))
                    .messagePublicDataDto(messagePublicDataDtos.get(idx))
                    .messageStatusDto(messageStatusDtos.get(idx))
                    .userInfoDto(authorDto)
                    .maxPages(maxPages)
                    .maxElements(maxElements).build());
        });
        return messageInfoDtos;
    }

    @Override
    public MessageInfoDto getPinnedMsgInfoDto(String userId) {
        Long pinnedMessageId = userClient.getPinnedMessageId(userId);
        // 未设置pinnedMsgId
        if(null == pinnedMessageId)
            return null;
        Optional<MessageEntity> opMsg = messageRepository.findById(pinnedMessageId);
        if(opMsg.isPresent()){
            MessageEntity messageEntity = opMsg.get();
            Optional<MessagePublicDataEntity> opPublicDataEntity = publicDataRepository.findById(pinnedMessageId);
            MessagePublicDataEntity messagePublicDataEntity = opPublicDataEntity.get();
            UserInfoDto userInfoDto = userClient.getUserInfoDtoById(userId);
            MessageStatusDto messageStatusDto = new MessageStatusDto();
            messageStatusDto.setLikeStatus(getLikeStatus(userId,pinnedMessageId));
            messageStatusDto.setRetweetStatus(getRetweetStatus(userId,pinnedMessageId));
//            Optional<LikeEntity> opLike = likeRepository.findById(new LikeEntity.LikeId(userId, pinnedMessageId));
//            MessageStatusDto messageStatusDto = new MessageStatusDto();
//            if(opLike.isPresent()){
//                LikeEntity likeEntity = opLike.get();
//                LikeStatus likeStatus = likeEntity.getLikeStatus();
//                if ((likeStatus == LikeStatus.like)) {
//                    messageStatusDto.setLikeStatus(true);
//                } else {
//                    messageStatusDto.setLikeStatus(false);
//                }
//                // 将点赞数据写入redis中（注：要本来就存在于数据库中，即你得点赞/取消点赞过）
//                redisService.transLikeToRedis(likeEntity);
//            }
//            else
//                messageStatusDto.setLikeStatus(false);
//            messageStatusDto.setRetweetStatus(false);
            return MessageInfoDto.builder()
                    .messageDto(messageConvertor.convertToDTO(messageEntity))
                    .messagePublicDataDto(messagePublicDataConvertor.convertToDTO(messagePublicDataEntity))
                    .messageStatusDto(messageStatusDto)
                    .userInfoDto(userInfoDto).build();
        }
        // pinnedMsgId错误,即不存在
        return null;
    }

    @Override
    public Boolean deleteMessageById(Long messageId) {
        Optional<MessageEntity> opMessage = messageRepository.findById(messageId);
        if(opMessage.isPresent()){
            MessageEntity messageEntity = opMessage.get();
            // 若是置顶消息则移除之
            userClient.removePinnedMessageId(messageEntity.getAuthorId(),messageId);
            // 若是评论型消息则减少一条conversationId的评论数
            if(messageEntity.getType()==MessageTypeEnum.replied_to){
                decrementReplyCount(messageEntity.getConversationId());
            }
            // 若是转发型消息则减少一条conversationId的转发数
            if(messageEntity.getType() == MessageTypeEnum.retweeted){
                decrementRetweetCount(messageEntity.getConversationId());
            }
            // 删除本条消息
            messageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }

    @Override
    public List<MessageInfoDto> getReplies(String inReplyToAuthorId, Long conversationId) {
        List<MessageEntity> entities = messageRepository.
                findByConversationIdAndInReplyToAuthorIdAndType(conversationId, inReplyToAuthorId,MessageTypeEnum.replied_to);
        if(CollectionUtil.isEmpty(entities)){
            return null;
        }
        Comparator<MessageEntity> timeComparator = (o1,o2) -> {
                Date d1 = o1.getCreateAt();
                Date d2 = o2.getCreateAt();
                if(d1.getTime()>d2.getTime())
                    return -1;
                else if(d1.getTime()<d2.getTime())
                    return 1;
                else
                    return 0;
        };
        // 对MessageEntities根据createAt进行倒序排序
        entities.sort(timeComparator);
        List<String> authorIds = new ArrayList<>();
        entities.forEach(e->{authorIds.add(e.getAuthorId());});
        List<UserInfoDto> userInfoDtos = userClient.getUserInfoDtos(authorIds);
        List<MessageDto> messageDtos = (List<MessageDto>) messageConvertor.convertToDTOList(entities);
        List<MessageInfoDto> infoDtos = new ArrayList<>();
        entities.forEach(e ->{
            int idx = entities.indexOf(e);
            MessageInfoDto infoDto = MessageInfoDto.builder()
                    .messageDto(messageDtos.get(idx))
                    .userInfoDto(userInfoDtos.get(idx)).build();
            infoDtos.add(infoDto);
        });
        return infoDtos;
    }

    @Override
    public MessageInfoDto getMessageInfoDto(Long messageId) {
        Optional<MessageEntity> opMessage = messageRepository.findById(messageId);
        if(!opMessage.isPresent())
            return null;
        MessageEntity messageEntity = opMessage.get();
        Optional<MessagePublicDataEntity> opPublicData = publicDataRepository.findById(messageId);
        MessagePublicDataEntity publicDataEntity = opPublicData.get();
        UserInfoDto userInfoDto = userClient.getUserInfoDtoById(messageEntity.getAuthorId());
        LoginVal loginVal = OauthUtils.getCurrentUser();
        UserInfoDto currentUser = userClient.getUserInfoDto(loginVal.getUsername());
        MessageStatusDto statusDto = new MessageStatusDto();
        statusDto.setLikeStatus(getLikeStatus(currentUser.getId(),messageId));
        statusDto.setRetweetStatus(getRetweetStatus(currentUser.getId(),messageId));
        return MessageInfoDto.builder().messageDto(messageConvertor.convertToDTO(messageEntity))
                .messagePublicDataDto(messagePublicDataConvertor.convertToDTO(publicDataEntity))
                .userInfoDto(userInfoDto)
                .messageStatusDto(statusDto).build();
    }

    // 专供publish用
    @Override
    public MessageInfoDto getMessageInfoDto(MessageEntity entity) {
        Long messageId = entity.getId();
        Optional<MessagePublicDataEntity> opPublicData = publicDataRepository.findById(messageId);
        MessagePublicDataEntity publicDataEntity = opPublicData.get();
        String authorId = entity.getAuthorId();
        UserInfoDto userInfoDto = userClient.getUserInfoDtoById(authorId);
        MessageStatusDto messageStatusDto = new MessageStatusDto();
        messageStatusDto.setLikeStatus(getLikeStatus(authorId,messageId));
        messageStatusDto.setRetweetStatus(getRetweetStatus(authorId,messageId));
        return MessageInfoDto.builder()
                .messageDto(messageConvertor.convertToDTO(entity))
                .messagePublicDataDto(messagePublicDataConvertor.convertToDTO(publicDataEntity))
                .userInfoDto(userInfoDto)
                .messageStatusDto(messageStatusDto)
                .build();
    }

    @Override
    public MessageInfoDto getRetweetedInfoDto(Long conversationId) {
        Optional<MessageEntity> opMessage = messageRepository.findById(conversationId);
        if(opMessage.isPresent()){
            MessageEntity messageEntity = opMessage.get();
            String authorId = messageEntity.getAuthorId();
            UserInfoDto userInfoDto = userClient.getUserInfoDtoById(authorId);
            return MessageInfoDto.builder()
                    .messageDto(messageConvertor.convertToDTO(messageEntity))
                    .userInfoDto(userInfoDto).build();
        }
        return null;
    }

    // 根据userId和pageNum获取对应的MessageEntity
    private Page<MessageEntity> getPageByAuthorIdAndPageNum(String authorId,Integer pageNum){
        // 根据createAt倒序排列
        Sort sort = Sort.by(Sort.Direction.DESC,"createAt");
        PageRequest pageRequest = PageRequest.of(pageNum,2,sort);
        return messageRepository.findByAuthorId(authorId, pageRequest);
    }

    // 更新评论数【增加】
    private void incrementReplyCount(Long messageId){
        Optional<MessagePublicDataEntity> opMessage = publicDataRepository.findById(messageId);
        if(!opMessage.isPresent())
            return;
        MessagePublicDataEntity publicDataEntity = opMessage.get();
        Long replyCount = publicDataEntity.getReplyCount();
        publicDataEntity.setReplyCount(replyCount+1);
        publicDataRepository.save(publicDataEntity);
    }

    // 更新评论数【减少】
    private void decrementReplyCount(Long messageId){
        Optional<MessagePublicDataEntity> opMessage = publicDataRepository.findById(messageId);
        if(!opMessage.isPresent())
            return;
        MessagePublicDataEntity entity = opMessage.get();
        entity.setReplyCount(entity.getReplyCount()-1);
        publicDataRepository.save(entity);
    }

    // 更新转发数【增加】 这里的messageId是type为retweeted里的conversationId
    private void incrementRetweetCount(Long messageId){
        Optional<MessagePublicDataEntity> opEntity = publicDataRepository.findById(messageId);
        if(!opEntity.isPresent())
            return;
        MessagePublicDataEntity entity = opEntity.get();
        entity.setRetweetCount(entity.getRetweetCount()+1);
        publicDataRepository.save(entity);
    }

    // 更新转发数【减少】
    private void decrementRetweetCount(Long messageId) {
        Optional<MessagePublicDataEntity> opEntity = publicDataRepository.findById(messageId);
        if (!opEntity.isPresent())
            return;
        MessagePublicDataEntity e = opEntity.get();
        e.setRetweetCount(e.getRetweetCount()-1);
        publicDataRepository.save(e);
    }

    private Boolean getLikeStatus(String userId,Long messageId){
        LikeEntity.LikeId likeId = new LikeEntity.LikeId(userId,messageId);
        Optional<LikeEntity> opLike = likeRepository.findById(likeId);
        if(opLike.isPresent()){
            LikeEntity likeEntity = opLike.get();
            LikeStatus likeStatus = likeEntity.getLikeStatus();
            // 将点赞数据写入redis中（注：要本来就存在于数据库中，即你得点赞/取消点赞过）
//            redisService.transLikeToRedis(likeEntity);
            return likeStatus == LikeStatus.like;
        }
        return false;
    }

    private Boolean getRetweetStatus(String authorId,Long conversationId){
        MessageEntity e = messageRepository.findByAuthorIdAndConversationIdAndType(authorId, conversationId, MessageTypeEnum.retweeted);
        return !ObjectUtil.isEmpty(e);
    }
}
