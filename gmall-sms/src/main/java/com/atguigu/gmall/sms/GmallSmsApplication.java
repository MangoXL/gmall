package com.atguigu.gmall.sms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
@MapperScan("com.atguigu.gmall.sms.mapper")
public class GmallSmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallSmsApplication.class, args);
	}

}
