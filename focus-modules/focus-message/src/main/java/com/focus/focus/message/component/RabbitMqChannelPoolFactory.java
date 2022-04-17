package com.focus.focus.message.component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 自定义ChannelPoolFactory，防止新建销毁造成的网络资源损耗
public class RabbitMqChannelPoolFactory implements PooledObjectFactory<Channel>{
    private Connection connection;

    @Value("${spring.rabbitmq.host:127.0.0.1}")
    private String host;
    @Value("${spring.rabbitmq.port:5672}")
    private int port;
    @Value("${spring.rabbitmq.username:guest}")
    private String username;
    @Value("${spring.rabbitmq.password:guest}")
    private String password;


    public RabbitMqChannelPoolFactory(){
        try {
            // 创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            // 设置工厂属性
            factory.setHost("127.0.0.1");
            factory.setPort(5672);
            factory.setUsername("ayyHA");
            factory.setPassword("123456aa");
            factory.setVirtualHost("/");
            factory.setConnectionTimeout(15000);
            // 获取connection实例，相当于一条TCP连接[测试注意心跳是否为60,若是，则需设置requestHeartbeat]
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    // PooledObject实例化资源
    @Override
    public PooledObject<Channel> makeObject() throws Exception {
        return new DefaultPooledObject<>(connection.createChannel());
    }

    // PooledObject销毁资源
    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception {
        if(p != null && p.getObject() != null && p.getObject().isOpen()){
            p.getObject().close();
        }
    }

    // 验证资源可用性
    @Override
    public boolean validateObject(PooledObject<Channel> p) {
        return p.getObject() != null && p.getObject().isOpen();
    }

    @Override
    public void activateObject(PooledObject<Channel> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<Channel> pooledObject) throws Exception {

    }
}
