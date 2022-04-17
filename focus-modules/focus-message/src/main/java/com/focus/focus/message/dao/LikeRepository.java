package com.focus.focus.message.dao;

import com.focus.focus.api.enumerate.LikeStatus;
import com.focus.focus.api.enumerate.MessageOperateStatus;
import com.focus.focus.message.domain.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, LikeEntity.LikeId> {
    List<LikeEntity> findByStatusAndLikeStatus(MessageOperateStatus status, LikeStatus likeStatus);
    List<LikeEntity> findByIdMessageIdAndStatusAndLikeStatus(Long messageId,MessageOperateStatus status,LikeStatus likeStatus);
//    List<LikeEntity> findAllByIdMessageId(Iterable<Long> it);
}
