package com.focus.focus.message.component;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

// 对ChannelPool的Channel进行管理
public class RabbitMqChannelService {
    private RabbitMqChannelPool pool;

    private RabbitMqChannelService(){
        initPool();
    }

    private void initPool(){
        RabbitMqChannelPoolFactory factory = new RabbitMqChannelPoolFactory();
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(300);
        // 最大空闲连接数
        config.setMaxIdle(20);
        // 最小空闲连接数
        config.setMinIdle(10);
        // 空闲连接检测周期
        config.setTimeBetweenEvictionRunsMillis(6000);
        // 达到空闲时间则连接移除
        config.setSoftMinEvictableIdleTimeMillis(20000);
        // 连接资源耗尽后最大等待时间
        config.setMaxWaitMillis(10000);
        // new Pool
        pool = new RabbitMqChannelPool(factory,config);
    }

    public static RabbitMqChannelService getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder{
        private final static RabbitMqChannelService INSTANCE = new RabbitMqChannelService();
    }

    // 获取channel对象，其中borrowObject是线程安全的
    public Channel getChannel() throws Exception {
        return pool.borrowObject();
    }

    // 归还channel对象
    public void returnChannel(Channel channel){
        pool.returnObject(channel);
    }

}
