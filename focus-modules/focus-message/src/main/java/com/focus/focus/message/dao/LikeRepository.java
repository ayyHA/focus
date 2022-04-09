package com.focus.focus.message.dao;

import com.focus.focus.message.domain.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, LikeEntity.LikeId> {
}
