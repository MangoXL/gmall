package com.atguigu.rabbit;

import com.atguigu.rabbit.bean.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitDemoApplicationTests {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	public void contextLoads() {
		User user = new User("admin", 19);
		rabbitTemplate.convertAndSend("exchange.topic","atguigu.111",user);
		System.out.println("消息发送完成！！");
	}

}
