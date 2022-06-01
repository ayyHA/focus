package com.focus.focus.message.dao;

import com.focus.focus.message.domain.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity,Long> {
    List<MessageEntity> findByAuthorId(String userId);
    List<MessageEntity> findByKeywordsContaining(String keywords);
    Page<MessageEntity> findByAuthorId(String authorId, Pageable pageable);
}
