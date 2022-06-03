package com.focus.focus.pay.controller;

import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.pay.service.IPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping
@RestController
public class PayController {
    @Autowired
    private IPayService payService;

    @PostMapping("/doReward")
    public ResponseEntity<ResponseMsg> doReward(@RequestParam("sourceId")String sourceId,
                                                @RequestParam("targetId")String targetId,
                                                @RequestParam("amountOfCoin")Long amountOfCoin){
        Boolean payStatus = payService.doReward(sourceId, targetId, amountOfCoin);
        if(!payStatus)
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.PAY_ERROR.getCode(),
                    ResponseCode.PAY_ERROR.getMsg(),false));
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.PAY_SUCCESS.getCode(),
                ResponseCode.PAY_SUCCESS.getMsg(),amountOfCoin));
    }
}
