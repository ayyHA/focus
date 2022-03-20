package com.focus.focus.user.dao;

import com.focus.focus.user.domain.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, FriendshipEntity.FriendshipId> {
}
