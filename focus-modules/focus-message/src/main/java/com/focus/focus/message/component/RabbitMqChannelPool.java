package com.focus.focus.message.component;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

// 自定义ChannelPool
public class RabbitMqChannelPool extends GenericObjectPool<Channel> {
    public RabbitMqChannelPool(PooledObjectFactory<Channel> factory) {
        super(factory);
    }

    public RabbitMqChannelPool(PooledObjectFactory<Channel> factory, GenericObjectPoolConfig<Channel> config) {
        super(factory, config);
    }

    public RabbitMqChannelPool(PooledObjectFactory<Channel> factory, GenericObjectPoolConfig<Channel> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
