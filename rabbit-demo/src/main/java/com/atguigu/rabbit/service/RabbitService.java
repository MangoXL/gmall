package com.atguigu.rabbit.service;

import com.atguigu.rabbit.bean.User;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RabbitService {

    @RabbitListener(queues = "atguigu.emps")
    public void consumer1(User user,Channel channel,Message message) throws IOException {
        System.out.println("收到消息" + user);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


    //@RabbitListener(queues = "atguigu")
    public void consumer(User user, Channel channel, Message message) throws IOException {
        try{
            if(user.getAge() % 2 == 0){
                System.out.println("1号机器收到消息" + user);
            }else {
                throw new RuntimeException();
            }
        }catch (Exception e){
            System.out.println("1号故障！");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    //@RabbitListener(queues = "atguigu")
    public void consumer2(User user, Channel channel, Message message) throws IOException {
        try{
            if(user.getAge() % 2 != 0){
                System.out.println("2号机器收到消息" + user);
            }else {
                throw new RuntimeException();
            }
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }
}
