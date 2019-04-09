package com.atguigu.gmall.order.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 订单广播交换机
     * @return
     */
    @Bean
    public Exchange orderFanoutExchange(){
        return new FanoutExchange("orderFanoutExchange",true,false);
    }

    /**
     *死信交换机
     * @return
     */
    @Bean
    public Exchange orderDeadExchange(){
        return new DirectExchange("orderDeadExchange",true,false);
    }

    /**
     * 库存系统接受订单消息的队列
     * @return
     */
    @Bean
    public Queue stockOrderQueue(){
        return new Queue("stockOrderQueue",true,false,false);
    }

    /**
     * 用户系统接收订单消息队列
     * @return
     */
    @Bean
    public Queue userOrderQueue(){
        return new Queue("userOrderQueue",true,false,false);
    }

    /**
     * 订单的延迟队列  ttl\dlx\dlk
     * @return
     */
    @Bean
    public Queue delayOrderQueue(){
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","orderDeadExchange");
        arguments.put("x-dead-letter-routing-key","dead.order");
        arguments.put("x-message-ttl",1000 * 60 * 30);
        Queue queue = new Queue("delayOrderQueue",true,false,false,arguments);
        return queue;
    }

    /**
     * 所有过期订单队列
     * @return
     */
    @Bean
    public Queue deadOrderQueue(){
        return new Queue("deadOrderQueue",true,false,false);
    }

    @Bean
    public Binding stockOrderQueueBinding(){
        return new Binding("stockOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderFanoutExchange",
                "",null);
    }

    @Bean
    public Binding userOrderQueueBinding(){
        return new Binding("userOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderFanoutExchange",
                "",null);
    }

    @Bean
    public Binding OrderDelayBinding(){
        return new Binding("delayOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderFanoutExchange",
                "",null);
    }

    @Bean
    public Binding deadBinding(){
        return new Binding("deadOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderDeadExchange",
                "dead.order",null);
    }
}
