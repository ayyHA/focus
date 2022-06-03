package com.focus.focus.pay.service;

public interface IPayService {
    Boolean doReward(String sourceId,String targetId,Long amountOfCoin);
}
