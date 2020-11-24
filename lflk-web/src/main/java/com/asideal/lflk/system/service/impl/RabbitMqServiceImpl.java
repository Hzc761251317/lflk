package com.asideal.lflk.system.service.impl;

import com.asideal.lflk.system.entity.TbMessage;
import com.asideal.lflk.system.mapper.TbMessageMapper;
import com.asideal.lflk.system.service.RabbitMqService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RabbitMqServiceImpl extends ServiceImpl<TbMessageMapper, TbMessage> implements RabbitMqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AmqpAdmin rabbitAdmin;



    /**
     * 直连式（简单式）
     * @param queueName
     * @param message
     */
    @Override
    public void sendMessageByWork(String queueName,Object message) {
        rabbitTemplate.convertAndSend(queueName,message);
    }

    /**
     * 发布/订阅模式(广播式)
     * @param exchange
     * @param message
     */
    @Override
    public void sendMessageByFanout(String exchange,Object message) {
        rabbitTemplate.convertAndSend(exchange,"",message);
    }

    /**
     * 路由模式Routing
     * @param exchange
     * @param message
     */
    @Override
    public void sendMessageByDirect(String exchange,String routingKey,Object message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    /**
     * Topics（通配符模式）
     * @param exchange
     * @param message
     */
    @Override
    public void sendMessageByTopic(String exchange,String routingKey,Object message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }


    @Override
    public void createExchange(String exchangeName, boolean durable, boolean autoDelete) {
        TopicExchange topicExchange = new TopicExchange(exchangeName, durable, autoDelete);
        rabbitAdmin.declareExchange(topicExchange);
    }

    @Override
    public void deleteExchange(String exchangeName) {
        rabbitAdmin.deleteExchange(exchangeName);
    }

    @Override
    public void createQueue(String queueName, boolean durable, boolean exclusive, boolean autoDelete) {
        Queue queue = new Queue(queueName, durable, exclusive, autoDelete);
        rabbitAdmin.declareQueue(queue);
    }

    @Override
    public void deleteQueue(String queueName) {
        rabbitAdmin.deleteQueue(queueName);
    }

    @Override
    public void bind(String queueName, String exchangeName, String routingKey) {
        rabbitAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null));
    }
}
