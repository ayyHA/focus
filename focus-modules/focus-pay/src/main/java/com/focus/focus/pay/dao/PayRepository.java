package com.focus.focus.pay.dao;

import com.focus.focus.pay.domain.entity.PayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRepository extends JpaRepository<PayEntity,Long> {
}
