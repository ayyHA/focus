package com.focus.focus.gateway.service.impl;

import com.focus.auth.common.model.SysConstant;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InitService {
    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        redisTemplate.opsForHash().put(SysConstant.OAUTH_URLS,"/**", Lists.newArrayList("ROLE_admin","ROLE_user"));
    }

}
