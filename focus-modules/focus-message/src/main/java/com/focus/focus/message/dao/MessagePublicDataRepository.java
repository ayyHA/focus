package com.focus.focus.message.dao;

import com.focus.focus.message.domain.entity.MessagePublicDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagePublicDataRepository extends JpaRepository<MessagePublicDataEntity,Long> {
//    List<MessagePublicDataEntity> findAllByOrderByMessageIdDesc(Iterable<Long> ids);
}
