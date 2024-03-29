package com.atguigu.gmall.ums;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
@MapperScan("com.atguigu.gmall.ums.mapper")
public class GmallUmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallUmsApplication.class, args);
	}

}
