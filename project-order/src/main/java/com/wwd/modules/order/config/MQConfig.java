package com.wwd.modules.order.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.wwd.modules.order.dto.OrderDTO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    //↓
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange", true, false);
    }
    //↓
    @Bean
    public Binding orderCreateOrderBinding(){
        return new Binding("oreder.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }
    //↓
    @Bean
    public Queue orderDelayQueue(){

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);
        Queue queue = new Queue("oreder.delay.queue", true, false, false, arguments);
        return queue;
    }
    //↓
    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding(
                "oreder.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }
    //↓
    @Bean
    public Queue orderReleaseQueue(){
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    /**
     * 接收订单过期关闭消息
     * @param orderDTO
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderDTO orderDTO, Channel channel, Message message) throws IOException {
        System.out.println("收到订单过期消息，准备关闭订单：" + orderDTO.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }




}





















