package com.focus.focus.message.domain.listener;

import com.focus.focus.message.domain.entity.MessageEntity;
import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
public class MessageAuditingListener {
    @PrePersist
    public void onPreSave(MessageEntity messageEntity){
        log.info("onPreSave => message: [{}]",messageEntity.getText());
        log.info("onPreSave => messageId: [{}]",messageEntity.getId());
        MessagePublicDataEntity messagePublicDataEntity = MessagePublicDataEntity.builder()
                .messageEntity(messageEntity)
                .likeCount(0L)
                .replyCount(0L)
                .retweetCount(0L)
                .build();
        messageEntity.setMessagePublicDataEntity(messagePublicDataEntity);
    }

    @PreUpdate
    public void onPreUpdate(MessageEntity messageEntity){
        log.info("onPreUpdate => message: [{}]",messageEntity.getText());
    }

    @PreRemove
    public void onPreRemove(MessageEntity messageEntity){
        log.info("onPreRemove => message: [{}]",messageEntity.getText());
    }

    @PostPersist
    public void onPostSave(MessageEntity messageEntity){
        log.info("onPostSave => messageId: [{}]",messageEntity.getId());
        log.info("onPostSave => message: [{}]",messageEntity.getText());
    }

    @PostRemove
    public void onPostRemove(MessageEntity messageEntity){
        log.info("onPostRemove => message: [{}]",messageEntity.getText());
    }
}
