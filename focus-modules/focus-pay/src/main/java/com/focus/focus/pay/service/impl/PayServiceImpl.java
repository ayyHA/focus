package com.focus.focus.pay.service.impl;

import com.focus.focus.api.enumerate.PayStatus;
import com.focus.focus.api.feign.UserClient;
import com.focus.focus.pay.dao.PayRepository;
import com.focus.focus.pay.domain.entity.PayEntity;
import com.focus.focus.pay.service.IPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements IPayService {
    private final UserClient userClient;
    @Autowired
    private PayRepository payRepository;
    @Override
    public Boolean doReward(String sourceId, String targetId, Long amountOfCoin) {
        Boolean rewardStatus = userClient.doReward(sourceId, targetId, amountOfCoin);
        PayStatus payStatus = rewardStatus?PayStatus.pay_success:PayStatus.pay_failed;
        PayEntity payEntity = PayEntity.builder()
                .payId(new PayEntity.PayId(sourceId,targetId))
                .amountOfCoin(amountOfCoin)
                .status(payStatus).build();
        payRepository.save(payEntity);
        return rewardStatus;
    }
}
