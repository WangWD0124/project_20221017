package com.wwd.modules.ware.service.listener;

import com.rabbitmq.client.Channel;
import com.wwd.modules.ware.service.WareSkuService;
import com.wwd.modules.ware.vo.StockLockedToMQVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@RabbitListener(queues = "stock.release.stock.queue")//监听解锁消息队列
@Service
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void handle(StockLockedToMQVo stockLockedToMQVo, Message message, Channel channel) throws IOException {
        System.out.println("库存服务收到解锁库存消息，正在处理工作单：" + stockLockedToMQVo.getTaskId());
        try {
            wareSkuService.StockRelease(stockLockedToMQVo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//解锁库存成功确认
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);//解锁失败确认，消息返回队列重试
        }
    }
}
