package com.focus.focus.message.component;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

// 用于将消息发送到指定Exchange中，并携带自身的RoutingKey，以匹配对应的Queue
@Slf4j
public class RabbitMqProducer {
    public static void sendMessage(String exchangeName, BuiltinExchangeType type,
                                   String queueName,String routingKey,Object object){
        RabbitMqChannelService channelService = RabbitMqChannelService.getInstance();
        Channel channel = null;
        try{
            channel = channelService.getChannel();
            // 为channel声明exchange,如果此exchange不存在,将在RabbitMQServer上创建;durably:true
            channel.exchangeDeclare(exchangeName,type,true);
            // 为channel声明一个queue,如果此queue不存在，将在Server上创建;durably:true,exclusive:false,auto delete:false
            channel.queueDeclare(queueName,true,false,false,null);
            // 将queue绑定到exchange上,类似于路由表,通过routing key索引
            channel.queueBind(queueName,exchangeName,routingKey);
            // 发送消息[看一下是否可以正常序列化，否则设置basicProperties中的contentType为json的converter,目前是使得消息可以持久化保存，重启依旧在]
            channel.basicPublish(exchangeName,routingKey, null,object.toString().getBytes());
            log.info(exchangeName,type,queueName,routingKey);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            e.printStackTrace();
        } finally {
            if(null != channel)
                channelService.returnChannel(channel);
        }
    }

}
