package com.atguigu.gmall.pms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class GmallJedisConfig {
    @Value("${spring.redis.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private Integer port;
    @Value("${spring.redis.jedis.timeout:3000}")
    private Integer timeout;
    @Value("${spring.redis.jedis.pool.max-idle:8}")
    private Integer maxIdle;
    @Value("${spring.redis.jedis.pool.min-idle:0}")
    private Integer minIdle;
    @Value("${spring.redis.jedis.poll.total:8}")
    private Integer maxTotal;

    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxTotal(maxTotal);

        JedisPool jedisPool = new JedisPool(config,host,port,timeout);
        return jedisPool;
    }
}
